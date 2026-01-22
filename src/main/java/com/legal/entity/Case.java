package com.legal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "cases", indexes = {
    @Index(name = "idx_case_number", columnList = "case_number"),
    @Index(name = "idx_client_id", columnList = "client_id"),
    @Index(name = "idx_advocate_id", columnList = "advocate_id"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_case_type", columnList = "case_type"),
    @Index(name = "idx_filing_date", columnList = "filing_date")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Case {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "case_id")
    private Long caseId;

    @Column(name = "case_number", unique = true, nullable = false, length = 100)
    private String caseNumber;

    @Column(name = "case_title", nullable = false, length = 300)
    private String caseTitle;

    @Enumerated(EnumType.STRING)
    @Column(name = "case_type", nullable = false)
    private CaseType caseType;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "advocate_id", nullable = false)
    private Advocate advocate;

    @Column(name = "court_name", length = 200)
    private String courtName;

    @Enumerated(EnumType.STRING)
    @Column(name = "court_type")
    private CourtType courtType;

    @Column(name = "filing_date")
    private LocalDate filingDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CaseStatus status = CaseStatus.OPEN;

    @Enumerated(EnumType.STRING)
    private Priority priority = Priority.MEDIUM;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "opposing_party", length = 300)
    private String opposingParty;

    @Column(name = "judge_name", length = 100)
    private String judgeName;

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

    public enum CaseType {
        CIVIL, CRIMINAL, FAMILY, CORPORATE, TAX, LABOUR, PROPERTY, OTHER
    }

    public enum CourtType {
        SUPREME_COURT, HIGH_COURT, DISTRICT_COURT, TRIBUNAL, OTHER
    }

    public enum CaseStatus {
        OPEN, IN_PROGRESS, CLOSED, WON, LOST, SETTLED
    }

    public enum Priority {
        LOW, MEDIUM, HIGH, URGENT
    }
}
