package com.legal.casemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legal.casemanagement.config.JwtAuthenticationFilter;
import com.legal.casemanagement.config.SecurityConfig;
import com.legal.casemanagement.dto.AuthDtos.LoginRequest;
import com.legal.casemanagement.dto.AuthDtos.LoginResponse;
import com.legal.casemanagement.service.AuthService;
import com.legal.casemanagement.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class })
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void login_validCredentials_returns200WithToken() throws Exception {
        LoginResponse mockResponse = LoginResponse.builder()
                .token("mock.jwt.token")
                .userId(1001L)
                .name("Admin User")
                .email("admin@legal.com")
                .role("ADMIN")
                .message("Login successful")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        LoginRequest request = new LoginRequest("admin@legal.com", "Admin123!");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("mock.jwt.token"))
                .andExpect(jsonPath("$.role").value("ADMIN"))
                .andExpect(jsonPath("$.message").value("Login successful"));
    }

    @Test
    void login_invalidCredentials_returns401() throws Exception {
        LoginResponse mockResponse = LoginResponse.builder()
                .message("Invalid password")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        LoginRequest request = new LoginRequest("admin@legal.com", "wrongpass");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid password"));
    }

    @Test
    void login_userNotFound_returns401() throws Exception {
        LoginResponse mockResponse = LoginResponse.builder()
                .message("User not found")
                .build();

        when(authService.login(any(LoginRequest.class))).thenReturn(mockResponse);

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new LoginRequest("no@email.com", "pass"))))
                .andExpect(status().isUnauthorized());
    }
}
