package com.legal.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents", indexes = {
    @Index(name = "idx_case_id", columnList = "case_id"),
    @Index(name = "idx_uploaded_by", columnList = "uploaded_by"),
    @Index(name = "idx_document_type", columnList = "document_type"),
    @Index(name = "idx_uploaded_at", columnList = "uploaded_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "case_id", nullable = false)
    private Case legalCase;

    @Column(name = "document_name", nullable = false, length = 300)
    private String documentName;

    @Enumerated(EnumType.STRING)
    @Column(name = "document_type")
    private DocumentType documentType = DocumentType.OTHER;

    @Column(name = "file_path", nullable = false, length = 500)
    private String filePath;

    @Column(name = "file_size")
    private Long fileSize;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    private User uploadedBy;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "uploaded_at", nullable = false, updatable = false)
    private LocalDateTime uploadedAt;

    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    public enum DocumentType {
        PETITION, EVIDENCE, AFFIDAVIT, ORDER, NOTICE, OTHER
    }
}
