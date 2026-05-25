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
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        // Enforcing strength 12 BCrypt as specified in architectural goals
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
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

                        // 4. Added: Open the layout engine endpoints to match team authorization patterns
                        .requestMatchers("/api/v1/dashboard/**").permitAll()

                        // Any other administrative edge targets fall back under standard security rules
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}