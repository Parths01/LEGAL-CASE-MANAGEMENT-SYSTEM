package com.legal.casemanagement.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.legal.casemanagement.config.JwtAuthenticationFilter;
import com.legal.casemanagement.config.SecurityConfig;
import com.legal.casemanagement.dto.UserDtos.CreateUserRequest;
import com.legal.casemanagement.entity.User;
import com.legal.casemanagement.repository.UserRepository;
import com.legal.casemanagement.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@Import({ SecurityConfig.class, JwtAuthenticationFilter.class })
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtUtil jwtUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private User sampleUser;

    @BeforeEach
    void setUp() {
        sampleUser = new User();
        sampleUser.setUserId(1001L);
        sampleUser.setEmail("admin@legal.com");
        sampleUser.setPassword("$2a$12$hashed");
        sampleUser.setName("Admin User");
        sampleUser.setRole("ADMIN");
        sampleUser.setPhone("9000000001");
        sampleUser.setAddress("1 Admin Lane");
        sampleUser.setStatus("ACTIVE");
        sampleUser.setCreatedAt(LocalDateTime.now());
        sampleUser.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllUsers_returnsUserList() throws Exception {
        when(userRepository.findAll()).thenReturn(Arrays.asList(sampleUser));

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("admin@legal.com"))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_existingId_returnsUser() throws Exception {
        when(userRepository.findById(1001L)).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(get("/api/users/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Admin User"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void getUserById_nonExistentId_returns404() throws Exception {
        when(userRepository.findById(9999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/9999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_validRequest_returns201() throws Exception {
        when(userRepository.findByEmail("newuser@legal.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("$2a$12$hashed");
        when(userRepository.save(any(User.class))).thenReturn(sampleUser);

        CreateUserRequest request = new CreateUserRequest(
                "newuser@legal.com", "Admin123!", "New User",
                "CLERK", "9000000099", "99 New St", "ACTIVE"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createUser_duplicateEmail_returns409() throws Exception {
        when(userRepository.findByEmail("admin@legal.com")).thenReturn(Optional.of(sampleUser));

        CreateUserRequest request = new CreateUserRequest(
                "admin@legal.com", "Admin123!", "Duplicate",
                "CLERK", "9000000099", "99 New St", "ACTIVE"
        );

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_existingId_returns200() throws Exception {
        when(userRepository.findById(1001L)).thenReturn(Optional.of(sampleUser));

        mockMvc.perform(delete("/api/users/1001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("User deleted successfully"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteUser_nonExistentId_returns404() throws Exception {
        when(userRepository.findById(9999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/users/9999"))
                .andExpect(status().isNotFound());
    }
}
