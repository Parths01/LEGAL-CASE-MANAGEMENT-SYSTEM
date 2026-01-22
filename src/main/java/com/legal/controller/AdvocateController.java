package com.legal.controller;

import com.legal.entity.Advocate;
import com.legal.repository.AdvocateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/advocates")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AdvocateController {

    @Autowired
    private AdvocateRepository advocateRepository;

    /**
     * Get all advocates
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE', 'CLIENT')")
    public ResponseEntity<List<Advocate>> getAllAdvocates() {
        try {
            List<Advocate> advocates = advocateRepository.findAll();
            return ResponseEntity.ok(advocates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get advocate by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE', 'CLIENT')")
    public ResponseEntity<Advocate> getAdvocateById(@PathVariable("id") Long id) {
        try {
            Optional<Advocate> advocate = advocateRepository.findById(id);
            return advocate.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create new advocate
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createAdvocate(@RequestBody Advocate advocate) {
        try {
            Advocate savedAdvocate = advocateRepository.save(advocate);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedAdvocate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating advocate: " + e.getMessage());
        }
    }

    /**
     * Update advocate
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE')")
    public ResponseEntity<?> updateAdvocate(@PathVariable("id") Long id, @RequestBody Advocate advocate) {
        try {
            Optional<Advocate> existingAdvocate = advocateRepository.findById(id);
            if (existingAdvocate.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Advocate advocateToUpdate = existingAdvocate.get();
            advocateToUpdate.setBarRegistrationNumber(advocate.getBarRegistrationNumber());
            advocateToUpdate.setSpecialization(advocate.getSpecialization());
            advocateToUpdate.setExperienceYears(advocate.getExperienceYears());
            advocateToUpdate.setQualification(advocate.getQualification());
            advocateToUpdate.setCourtPractice(advocate.getCourtPractice());

            Advocate updatedAdvocate = advocateRepository.save(advocateToUpdate);
            return ResponseEntity.ok(updatedAdvocate);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating advocate: " + e.getMessage());
        }
    }

    /**
     * Delete advocate
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteAdvocate(@PathVariable("id") Long id) {
        try {
            if (!advocateRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            advocateRepository.deleteById(id);
            return ResponseEntity.ok().body("Advocate deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting advocate: " + e.getMessage());
        }
    }

    /**
     * Get advocate by user ID
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE')")
    public ResponseEntity<Advocate> getAdvocateByUserId(@PathVariable("userId") Long userId) {
        try {
            Optional<Advocate> advocate = advocateRepository.findByUser_UserId(userId);
            return advocate.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get advocates by specialization
     */
    @GetMapping("/specialization/{specialization}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<List<Advocate>> getAdvocatesBySpecialization(@PathVariable("specialization") String specialization) {
        try {
            List<Advocate> advocates = advocateRepository.findBySpecializationContainingIgnoreCase(specialization);
            return ResponseEntity.ok(advocates);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
