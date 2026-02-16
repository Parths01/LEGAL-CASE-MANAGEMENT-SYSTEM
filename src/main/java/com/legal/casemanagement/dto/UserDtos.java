package com.legal.casemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class UserDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateUserRequest {
        private String email;
        private String password;
        private String name;
        private String role;
        private String phone;
        private String address;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateUserRequest {
        private String name;
        private String phone;
        private String address;
        private String status;
        private String role;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserDTO {
        private Long userId;
        private String email;
        private String name;
        private String role;
        private String phone;
        private String address;
        private String status;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
}
