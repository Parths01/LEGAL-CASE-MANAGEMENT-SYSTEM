package com.legal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "advocates", indexes = {
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_firm_id", columnList = "firm_id"),
    @Index(name = "idx_specialization", columnList = "specialization")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Advocate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "advocate_id")
    private Long advocateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "firm_id")
    private LawFirm lawFirm;

    @Column(name = "bar_registration_number", unique = true, length = 100)
    private String barRegistrationNumber;

    @Column(length = 200)
    private String specialization;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @Column(length = 200)
    private String qualification;

    @Column(name = "court_practice", length = 200)
    private String courtPractice;

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
