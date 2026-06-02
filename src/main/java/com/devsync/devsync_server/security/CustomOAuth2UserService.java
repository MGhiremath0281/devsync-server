package com.devsync.devsync_server.security;

import com.devsync.devsync_server.auth.entity.AuthProvider;
import com.devsync.devsync_server.auth.entity.Role;
import com.devsync.devsync_server.auth.entity.User;
import com.devsync.devsync_server.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oauthUser = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        String email;
        String name;
        String providerId;

        if ("google".equals(provider)) {
            email = oauthUser.getAttribute("email");
            name = oauthUser.getAttribute("name");
            providerId = oauthUser.getAttribute("sub");
        } else {
            email = oauthUser.getAttribute("email");
            name = oauthUser.getAttribute("login");
            Object githubId = oauthUser.getAttribute("id");
            providerId = (githubId != null) ? String.valueOf(githubId) : null;

            if (email == null || email.isEmpty()) {
                email = fetchGitHubEmail(userRequest.getAccessToken().getTokenValue());
            }
        }

        if (email == null || email.isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider: " + provider);
        }

        final String finalEmail = email;
        User user = userRepository.findByEmail(finalEmail)
                .orElseGet(() -> {
                    User newUser = User.builder()
                            .username(generateUsername(name))
                            .email(finalEmail)
                            .role(Role.MEMBER)
                            .provider("google".equals(provider) ? AuthProvider.GOOGLE : AuthProvider.GITHUB)
                            .providerId(providerId)
                            .build();

                    return userRepository.save(newUser);
                });

        return new OAuth2UserPrincipal(user, oauthUser.getAttributes());
    }
    private String fetchGitHubEmail(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<List<Map<String, Object>>> response = restTemplate.exchange(
                    "https://api.github.com/user/emails",
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            List<Map<String, Object>> emailsList = response.getBody();
            if (emailsList != null) {
                for (Map<String, Object> emailObj : emailsList) {
                    Boolean primary = (Boolean) emailObj.get("primary");
                    Boolean verified = (Boolean) emailObj.get("verified");
                    if (Boolean.TRUE.equals(primary) && Boolean.TRUE.equals(verified)) {
                        return (String) emailObj.get("email");
                    }
                }
            }
        } catch (Exception e) {
        }
        return null;
    }

    private String generateUsername(String base) {
        if (base == null) base = "User";
        return base.replaceAll("\\s+", "") + System.currentTimeMillis();
    }
}