package com.legal.repository;

import com.legal.entity.LawFirm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LawFirmRepository extends JpaRepository<LawFirm, Long> {
    
    Optional<LawFirm> findByFirmName(String firmName);
}
