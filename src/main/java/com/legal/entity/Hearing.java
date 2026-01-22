package com.legal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "hearings", indexes = {
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_hearing_date", columnList = "hearing_date"),
    @Index(name = "idx_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Hearing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "hearing_id")
    private Long hearingId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case legalCase;

    @Column(name = "hearing_date", nullable = false)
    private LocalDateTime hearingDate;

    @Column(length = 100)
    private String courtroom;

    @Enumerated(EnumType.STRING)
    @Column(name = "hearing_type")
    private HearingType hearingType = HearingType.OTHER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private HearingStatus status = HearingStatus.SCHEDULED;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "next_hearing_date")
    private LocalDateTime nextHearingDate;

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

    public enum HearingType {
        PRELIMINARY, EVIDENCE, ARGUMENTS, JUDGMENT, OTHER
    }

    public enum HearingStatus {
        SCHEDULED, COMPLETED, ADJOURNED, CANCELLED
    }
}
