package com.devsync.devsync_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/github/**",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/ws/**"
                        ).permitAll()
                        .anyRequest().permitAll()
                        // 1. Open the Auth Gateway so developers can register/login from the scratch
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()

                        // 2. Open up the multi-tenant workspace management endpoints
                        .requestMatchers("/api/teams/**").permitAll()
                        .requestMatchers("/api/channels/**").permitAll()

                        // 3. Keep the messaging and real-time streaming transport gates completely clear
                        .requestMatchers("/api/v1/messages/**").permitAll()
                        .requestMatchers("/ws-provider/**").permitAll()
                        .requestMatchers("/ws-raw/**").permitAll()

                        // Any other administrative edge targets fall back under standard security rules
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}