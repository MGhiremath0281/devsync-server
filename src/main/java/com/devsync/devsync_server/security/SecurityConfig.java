package com.devsync.devsync_server.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Prepares method-level @PreAuthorize security for later
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Enforcing strength 12 BCrypt as specified in architectural goals
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Disabled for stateless REST and WebSockets
                .authorizeHttpRequests(auth -> auth
                        // Allow public access to HTTP history endpoints and raw WebSocket frames during testing
                        .requestMatchers("/api/v1/messages/**").permitAll()
                        .requestMatchers("/ws-provider/**").permitAll()
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}