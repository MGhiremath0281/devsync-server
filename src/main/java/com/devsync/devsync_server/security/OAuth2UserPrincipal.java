package com.devsync.devsync_server.security;

import com.devsync.devsync_server.auth.entity.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Map;

public class OAuth2UserPrincipal
        extends CustomUserPrincipal
        implements OAuth2User {

    private final Map<String, Object> attributes;

    public OAuth2UserPrincipal(
            User user,
            Map<String, Object> attributes
    ) {
        super(user);
        this.attributes = attributes;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return getUser().getEmail();
    }
}