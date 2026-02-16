package com.legal.casemanagement.controller;

import com.legal.casemanagement.dto.UserDtos.CreateUserRequest;
import com.legal.casemanagement.dto.UserDtos.UpdateUserRequest;
import com.legal.casemanagement.dto.UserDtos.UserDTO;
import com.legal.casemanagement.entity.User;
import com.legal.casemanagement.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Get all users
     */
    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    /**
     * Get user by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> ResponseEntity.ok(convertToDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Create new user
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ErrorResponse("Email already exists"));
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setName(request.getName());
        user.setRole(request.getRole() != null ? request.getRole() : "CLERK");
        user.setPhone(request.getPhone());
        user.setAddress(request.getAddress());
        user.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToDTO(savedUser));
    }

    /**
     * Update user
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody UpdateUserRequest request) {
        return userRepository.findById(id)
                .map(user -> {
                    if (request.getName() != null) user.setName(request.getName());
                    if (request.getPhone() != null) user.setPhone(request.getPhone());
                    if (request.getAddress() != null) user.setAddress(request.getAddress());
                    if (request.getStatus() != null) user.setStatus(request.getStatus());
                    if (request.getRole() != null) user.setRole(request.getRole());
                    user.setUpdatedAt(LocalDateTime.now());

                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(convertToDTO(updatedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete user
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    userRepository.delete(user);
                    return ResponseEntity.ok(new SuccessResponse("User deleted successfully"));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get users by role
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable String role) {
        List<User> users = userRepository.findAll().stream()
                .filter(u -> u.getRole().equalsIgnoreCase(role))
                .collect(Collectors.toList());
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    /**
     * Get active users
     */
    @GetMapping("/status/active")
    public ResponseEntity<List<UserDTO>> getActiveUsers() {
        List<User> users = userRepository.findAll().stream()
                .filter(u -> "ACTIVE".equalsIgnoreCase(u.getStatus()))
                .collect(Collectors.toList());
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    /**
     * Update user status
     */
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestParam String status) {
        return userRepository.findById(id)
                .map(user -> {
                    user.setStatus(status);
                    user.setUpdatedAt(LocalDateTime.now());
                    User updatedUser = userRepository.save(user);
                    return ResponseEntity.ok(convertToDTO(updatedUser));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private UserDTO convertToDTO(User user) {
        return UserDTO.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .phone(user.getPhone())
                .address(user.getAddress())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    // Helper classes for responses
    static class ErrorResponse {
        public String message;
        public ErrorResponse(String message) {
            this.message = message;
        }
    }

    static class SuccessResponse {
        public String message;
        public SuccessResponse(String message) {
            this.message = message;
        }
    }
}
