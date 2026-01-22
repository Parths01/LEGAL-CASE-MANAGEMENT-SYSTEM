package com.legal.repository;

import com.legal.entity.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    
    List<Document> findByLegalCaseCaseId(Long caseId);
    
    List<Document> findByUploadedByUserId(Long userId);
    
    List<Document> findByDocumentType(Document.DocumentType documentType);
}
