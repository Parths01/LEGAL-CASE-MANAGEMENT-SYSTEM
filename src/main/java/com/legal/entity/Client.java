package com.legal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "clients", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_client_type", columnList = "client_type")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "client_id")
    private Long clientId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(length = 50)
    private String gstin;

    @Column(name = "pan_number", length = 20)
    private String panNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "client_type")
    private ClientType clientType = ClientType.INDIVIDUAL;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ClientType {
        INDIVIDUAL, CORPORATE, GOVERNMENT
    }
}
