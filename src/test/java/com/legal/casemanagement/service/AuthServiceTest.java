package com.legal.casemanagement.service;

import com.legal.casemanagement.dto.AuthDtos.LoginRequest;
import com.legal.casemanagement.dto.AuthDtos.LoginResponse;
import com.legal.casemanagement.entity.User;
import com.legal.casemanagement.repository.UserRepository;
import com.legal.casemanagement.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUserId(1001L);
        testUser.setEmail("admin@legal.com");
        testUser.setPassword("$2a$12$hashedPassword");
        testUser.setName("Admin User");
        testUser.setRole("ADMIN");
        testUser.setStatus("ACTIVE");
    }

    @Test
    void login_withValidCredentials_returnsSuccessResponse() {
        when(userRepository.findByEmail("admin@legal.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("Admin123!", "$2a$12$hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(any(), anyString(), anyString())).thenReturn("mock.jwt.token");

        LoginRequest request = new LoginRequest("admin@legal.com", "Admin123!");
        LoginResponse response = authService.login(request);

        assertEquals("Login successful", response.getMessage());
        assertEquals("mock.jwt.token", response.getToken());
        assertEquals("ADMIN", response.getRole());
        assertEquals("Admin User", response.getName());
        verify(userRepository, times(1)).findByEmail("admin@legal.com");
    }

    @Test
    void login_withNonExistentUser_returnsUserNotFoundMessage() {
        when(userRepository.findByEmail("no@email.com")).thenReturn(Optional.empty());

        LoginRequest request = new LoginRequest("no@email.com", "somepass");
        LoginResponse response = authService.login(request);

        assertEquals("User not found", response.getMessage());
        assertNull(response.getToken());
    }

    @Test
    void login_withInactiveUser_returnsInactiveMessage() {
        testUser.setStatus("INACTIVE");
        when(userRepository.findByEmail("admin@legal.com")).thenReturn(Optional.of(testUser));

        LoginRequest request = new LoginRequest("admin@legal.com", "Admin123!");
        LoginResponse response = authService.login(request);

        assertTrue(response.getMessage().contains("inactive") || response.getMessage().contains("not found"));
        assertNull(response.getToken());
    }

    @Test
    void login_withWrongPassword_returnsInvalidPasswordMessage() {
        when(userRepository.findByEmail("admin@legal.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("wrongpass", "$2a$12$hashedPassword")).thenReturn(false);

        LoginRequest request = new LoginRequest("admin@legal.com", "wrongpass");
        LoginResponse response = authService.login(request);

        assertEquals("Invalid password", response.getMessage());
        assertNull(response.getToken());
    }

    @Test
    void login_withNullRequest_returnsErrorMessage() {
        LoginResponse response = authService.login(null);
        assertNotNull(response.getMessage());
        assertNull(response.getToken());
    }

    @Test
    void login_withNullEmail_returnsErrorMessage() {
        LoginRequest request = new LoginRequest(null, "Admin123!");
        LoginResponse response = authService.login(request);
        assertNotNull(response.getMessage());
    }
}
