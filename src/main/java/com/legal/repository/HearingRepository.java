package com.legal.repository;

import com.legal.entity.Hearing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface HearingRepository extends JpaRepository<Hearing, Long> {
    
    List<Hearing> findByLegalCaseCaseId(Long caseId);
    
    List<Hearing> findByStatus(Hearing.HearingStatus status);
    
    @Query("SELECT h FROM Hearing h WHERE h.hearingDate BETWEEN :startDate AND :endDate")
    List<Hearing> findByHearingDateBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT h FROM Hearing h WHERE h.legalCase.advocate.advocateId = :advocateId AND h.hearingDate BETWEEN :startDate AND :endDate")
    List<Hearing> findTodaysHearingsByAdvocate(Long advocateId, LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT h FROM Hearing h WHERE h.status = 'SCHEDULED' AND h.hearingDate < :currentDate")
    List<Hearing> findOverdueHearings(LocalDateTime currentDate);
    
    @Query("SELECT COUNT(h) FROM Hearing h WHERE h.status = :status")
    Long countByStatus(Hearing.HearingStatus status);
}
