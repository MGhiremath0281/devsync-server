package com.devsync.devsync_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * ✅ FIX: Expose the AuthenticationManager as a bean.
     *
     * Without this, Spring Boot sees no AuthenticationManager and
     * auto-configures an inMemoryUserDetailsManager as a fallback.
     * That fallback partially re-enables default security filters
     * (form-login, CSRF on POSTs) even though we disabled them below,
     * which is why POST /api/v1/dashboard/create was returning 403.
     *
     * Declaring this bean tells Spring "we own auth — don't add anything."
     */
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // Disable CSRF — we use stateless JWT, no session cookies
                .csrf(AbstractHttpConfigurer::disable)

                // Disable Spring's default form-login and http-basic
                // so they can never intercept requests before our rules run
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // CORS
                .cors(cors -> cors.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOriginPatterns(List.of("*"));
                    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
                    config.setAllowedHeaders(List.of("*"));
                    config.setAllowCredentials(true);
                    return config;
                }))

                // Stateless — no HTTP session, ever
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Authorization rules
                .authorizeHttpRequests(auth -> auth

                        // Auth
                        .requestMatchers("/api/auth/**").permitAll()

                        // WebSocket
                        .requestMatchers("/ws/**", "/ws-provider/**", "/ws-raw/**").permitAll()

                        // Workspace
                        .requestMatchers("/api/teams/**", "/api/channels/**").permitAll()

                        // Messages
                        .requestMatchers("/api/v1/messages/**").permitAll()

                        // Dashboard  ← this covers /create, /summary, /track-access, etc.
                        .requestMatchers("/api/v1/dashboard/**").permitAll()

                        // Meetings
                        .requestMatchers("/api/meetings/**").permitAll()

                        // Actuator
                        .requestMatchers("/actuator/**").permitAll()

                        // Preflight
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        // Everything else requires auth
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}