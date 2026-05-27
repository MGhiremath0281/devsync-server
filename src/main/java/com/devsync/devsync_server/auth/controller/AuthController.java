package com.devsync.devsync_server.auth.controller;

import com.devsync.devsync_server.auth.dto.AuthResponse;
import com.devsync.devsync_server.auth.dto.LoginRequest;
import com.devsync.devsync_server.auth.dto.SignupRequest;
import com.devsync.devsync_server.auth.entity.User;
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
    public ResponseEntity<?> signup(@RequestBody SignupRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()
                || userRepository.findByUsername(request.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("User already exists");
        }

        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .build();

        User savedUser = userRepository.save(user);
        String token   = generateJwtToken(savedUser);

        return ResponseEntity.ok(
                new AuthResponse(token, savedUser.getId(), savedUser.getUsername(), savedUser.getEmail())
        );
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!user.getPassword().equals(request.getPassword())) {
            return ResponseEntity.status(401).body("Invalid password");
        }

        String token = generateJwtToken(user);

        return ResponseEntity.ok(
                new AuthResponse(token, user.getId(), user.getUsername(), user.getEmail())
        );
    }

    private String generateJwtToken(User user) {

        Key key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));

        long expirationTimeInMs = 86_400_000L; // 24 hours

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("firstName", user.getUsername())
                .claim("userId", (long) user.getId())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTimeInMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}