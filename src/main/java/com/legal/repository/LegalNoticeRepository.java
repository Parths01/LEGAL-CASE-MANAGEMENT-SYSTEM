package com.legal.repository;

import com.legal.entity.LegalNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LegalNoticeRepository extends JpaRepository<LegalNotice, Long> {
    
    List<LegalNotice> findByLegalCaseCaseId(Long caseId);
    
    List<LegalNotice> findByStatus(LegalNotice.NoticeStatus status);
    
    List<LegalNotice> findByNoticeType(LegalNotice.NoticeType noticeType);
}
