package com.legal.casemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class ClientDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateClientRequest {
        private String name;
        private String email;
        private String password;
        private String phone;
        private String address;
        private String clientType;
        private String companyName;
        private String gstin;
        private String panNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClientDTO {
        private Long clientId;
        private Long userId;
        private String name;
        private String email;
        private String phone;
        private String address;
        private String clientType;
        private String companyName;
        private String gstin;
        private String panNumber;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClientListItem {
        private Long clientId;
        private Long userId;
        private String name;
        private String email;
        private String clientType;
        private String companyName;
    }
}
