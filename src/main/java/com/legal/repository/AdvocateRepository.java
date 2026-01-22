package com.legal.repository;

import com.legal.entity.Advocate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AdvocateRepository extends JpaRepository<Advocate, Long> {
    
    Optional<Advocate> findByUserUserId(Long userId);
    
    Optional<Advocate> findByUser_UserId(Long userId);
    
    Optional<Advocate> findByBarRegistrationNumber(String barRegistrationNumber);
    
    List<Advocate> findByLawFirmFirmId(Long firmId);
    
    List<Advocate> findBySpecialization(String specialization);
    
    List<Advocate> findBySpecializationContainingIgnoreCase(String specialization);
}
