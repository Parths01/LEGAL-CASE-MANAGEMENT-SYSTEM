package com.legal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "law_firms", indexes = {
    @Index(name = "idx_firm_name", columnList = "firm_name")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LawFirm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "firm_id")
    private Long firmId;

    @Column(name = "firm_name", nullable = false, length = 200)
    private String firmName;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(length = 100)
    private String email;

    @Column(length = 20)
    private String phone;

    @Column(length = 200)
    private String website;

    @Column(name = "registration_number", length = 100)
    private String registrationNumber;

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
}
