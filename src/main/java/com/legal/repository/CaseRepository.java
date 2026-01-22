package com.legal.repository;

import com.legal.entity.Case;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CaseRepository extends JpaRepository<Case, Long> {

    interface CaseStatusCount {
        String getStatus();
        Long getTotal();
    }
    
    Optional<Case> findByCaseNumber(String caseNumber);
    
    boolean existsByCaseNumber(String caseNumber);
    
    List<Case> findByClient_ClientId(Long clientId);
    
    List<Case> findByClientClientId(Long clientId);
    
    List<Case> findByAdvocate_AdvocateId(Long advocateId);
    
    List<Case> findByAdvocateAdvocateId(Long advocateId);
    
    List<Case> findByStatus(Case.CaseStatus status);
    
    List<Case> findByCaseType(Case.CaseType caseType);
    
    List<Case> findByCaseNumberContainingIgnoreCaseOrCaseTitleContainingIgnoreCase(String caseNumber, String caseTitle);
    
    @Query("SELECT c FROM Case c WHERE c.status = :status AND c.advocate.advocateId = :advocateId")
    List<Case> findByAdvocateAndStatus(Long advocateId, Case.CaseStatus status);
    
    @Query("SELECT COUNT(c) FROM Case c WHERE c.status = :status")
    Long countByStatus(Case.CaseStatus status);
    
    @Query("SELECT COUNT(c) FROM Case c WHERE c.advocate.advocateId = :advocateId")
    Long countByAdvocate(Long advocateId);

    @Query("SELECT c.status AS status, COUNT(c) AS total FROM Case c GROUP BY c.status")
    List<CaseStatusCount> fetchStatusDistribution();
}
