package com.legal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_email", columnList = "email"),
    @Index(name = "idx_role", columnList = "role"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // Use DB default (CLIENT) unless explicitly set

    @Column(length = 20)
    private String phone;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status; // Use DB default (ACTIVE) unless explicitly set

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private LocalDateTime createdAt; // Let DB default CURRENT_TIMESTAMP set this

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt; // Let DB handle ON UPDATE CURRENT_TIMESTAMP

    public enum UserRole {
        ADMIN, ADVOCATE, CLIENT, CLERK
    }

    public enum UserStatus {
        ACTIVE, INACTIVE, SUSPENDED
    }
}
