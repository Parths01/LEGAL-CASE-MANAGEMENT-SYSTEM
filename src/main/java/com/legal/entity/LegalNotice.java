package com.legal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "legal_notices", indexes = {
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_sent_date", columnList = "sent_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LegalNotice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long noticeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case legalCase;

    @Enumerated(EnumType.STRING)
    @Column(name = "notice_type")
    private NoticeType noticeType = NoticeType.OTHER;

    @Column(name = "recipient_name", nullable = false, length = 200)
    private String recipientName;

    @Column(name = "recipient_address", columnDefinition = "TEXT")
    private String recipientAddress;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "sent_date")
    private LocalDate sentDate;

    @Enumerated(EnumType.STRING)
    private NoticeStatus status = NoticeStatus.DRAFT;

    @Column(name = "generated_at", nullable = false, updatable = false)
    private LocalDateTime generatedAt;

    @PrePersist
    protected void onCreate() {
        generatedAt = LocalDateTime.now();
    }

    public enum NoticeType {
        DEMAND, TERMINATION, CEASE_DESIST, EVICTION, OTHER
    }

    public enum NoticeStatus {
        DRAFT, SENT, ACKNOWLEDGED, RESPONDED
    }
}
