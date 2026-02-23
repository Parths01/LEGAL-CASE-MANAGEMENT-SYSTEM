package com.legal.casemanagement.service;

import com.legal.casemanagement.dto.AuthDtos.LoginRequest;
import com.legal.casemanagement.dto.AuthDtos.LoginResponse;
import com.legal.casemanagement.entity.User;
import com.legal.casemanagement.repository.UserRepository;
import com.legal.casemanagement.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        if (request == null || request.getEmail() == null || request.getPassword() == null) {
            return LoginResponse.builder()
                    .message("Email and password are required")
                    .build();
        }

        String email = request.getEmail().trim();
        String rawPassword = request.getPassword();

        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            return LoginResponse.builder()
                    .message("User not found")
                    .build();
        }

        User user = userOpt.get();
        if (!"ACTIVE".equalsIgnoreCase(user.getStatus())) {
            return LoginResponse.builder()
                    .message("User not found or account is inactive")
                    .build();
        }

        if (!isPasswordValidAndMigrateIfNeeded(user, rawPassword)) {
            return LoginResponse.builder()
                    .message("Invalid password")
                    .build();
        }

        String normalizedRole = user.getRole() == null
                ? ""
                : user.getRole().trim().toUpperCase(Locale.ROOT);

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), normalizedRole);

        return LoginResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(normalizedRole)
                .message("Login successful")
                .build();
    }

    private boolean isPasswordValidAndMigrateIfNeeded(User user, String rawPassword) {
        String storedPassword = user.getPassword();
        if (storedPassword == null || storedPassword.isBlank()) {
            return false;
        }

        if (storedPassword.startsWith("$2")) {
            return passwordEncoder.matches(rawPassword, storedPassword);
        }

        // Legacy plain-text compatibility: allow once and migrate to BCrypt.
        if (rawPassword.equals(storedPassword)) {
            user.setPassword(passwordEncoder.encode(rawPassword));
            userRepository.save(user);
            return true;
        }

        return false;
    }
}
