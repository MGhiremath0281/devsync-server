package com.devsync.devsync_server.auth.controller;

import com.devsync.devsync_server.auth.entity.User;
import com.devsync.devsync_server.auth.dto.AuthResponse;
import com.devsync.devsync_server.auth.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestParam String username, @RequestParam String email, @RequestParam String password) {
        if (userRepository.findByEmail(email).isPresent() || userRepository.findByUsername(username).isPresent()) {
            return ResponseEntity.badRequest().body("User identity attributes already exist inside DevSync!");
        }

        User user = User.builder()
                .username(username)
                .email(email)
                .password(password) // Clear text or BCrypt hashed depending on your preferences
                .build();
        User savedUser = userRepository.save(user);

        String token = generateJwtToken(savedUser);
        return ResponseEntity.ok(new AuthResponse(token, savedUser.getId(), savedUser.getUsername(), savedUser.getEmail()));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Invalid identity profile matching parameters."));

        if (!user.getPassword().equals(password)) {
            return ResponseEntity.status(401).body("Unauthorized: Credentials mismatch.");
        }

        String token = generateJwtToken(user);
        return ResponseEntity.ok(new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail()));
    }

    private String generateJwtToken(User user) {
        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
        long expirationTimeInMs = 86400000; // 24 Hours validity window

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getId()) // Injecting the user ID into the token structure
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}