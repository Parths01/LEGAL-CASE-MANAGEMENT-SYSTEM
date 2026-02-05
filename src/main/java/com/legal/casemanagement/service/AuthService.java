package com.legal.casemanagement.service;

import com.legal.casemanagement.dto.LoginRequest;
import com.legal.casemanagement.dto.LoginResponse;
import com.legal.casemanagement.entity.User;
import com.legal.casemanagement.repository.UserRepository;
import com.legal.casemanagement.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        Optional<User> userOpt = userRepository.findByEmailAndStatus(request.getEmail(), "ACTIVE");

        if (userOpt.isEmpty()) {
            return LoginResponse.builder()
                    .message("User not found or account is inactive")
                    .build();
        }

        User user = userOpt.get();

        // Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return LoginResponse.builder()
                    .message("Invalid password")
                    .build();
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(user.getUserId(), user.getEmail(), user.getRole());

        return LoginResponse.builder()
                .token(token)
                .userId(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .message("Login successful")
                .build();
    }
}
