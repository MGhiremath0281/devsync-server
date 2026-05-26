package com.devsync.devsync_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
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

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http

                // Disable CSRF
                .csrf(AbstractHttpConfigurer::disable)

                // Enable CORS
                .cors(cors -> cors.configurationSource(request -> {

                    CorsConfiguration configuration =
                            new CorsConfiguration();

                    configuration.setAllowedOriginPatterns(
                            List.of(
                                    "*"
                            )
                    );

                    configuration.setAllowedMethods(
                            List.of(
                                    "GET",
                                    "POST",
                                    "PUT",
                                    "DELETE",
                                    "PATCH",
                                    "OPTIONS"
                            )
                    );

                    configuration.setAllowedHeaders(
                            List.of("*")
                    );

                    configuration.setAllowCredentials(true);

                    return configuration;
                }))

                // Stateless JWT Architecture
                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                // Authorization Rules
                .authorizeHttpRequests(auth -> auth

                        // Auth APIs
                        .requestMatchers(
                                "/api/auth/**"
                        ).permitAll()

                        // WebSocket Endpoints
                        .requestMatchers(
                                "/ws/**",
                                "/ws-provider/**",
                                "/ws-raw/**"
                        ).permitAll()

                        // Workspace APIs
                        .requestMatchers(
                                "/api/teams/**",
                                "/api/channels/**"
                        ).permitAll()

                        // Message APIs
                        .requestMatchers(
                                "/api/v1/messages/**"
                        ).permitAll()

                        // Dashboard APIs
                        .requestMatchers(
                                "/api/v1/dashboard/**"
                        ).permitAll()

                        // Meeting APIs
                        .requestMatchers(
                                "/api/meetings/**"
                        ).permitAll()

                        // Actuator APIs
                        .requestMatchers(
                                "/actuator/**"
                        ).permitAll()

                        // Preflight Requests
                        .requestMatchers(
                                HttpMethod.OPTIONS,
                                "/**"
                        ).permitAll()

                        // Remaining APIs
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}