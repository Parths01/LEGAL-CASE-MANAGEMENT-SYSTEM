package com.legal.controller;

import com.legal.entity.Case;
import com.legal.repository.CaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api/cases")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CaseController {

    @Autowired
    private CaseRepository caseRepository;

    /**
     * Get all cases
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE', 'CLIENT')")
    public ResponseEntity<List<Case>> getAllCases() {
        try {
            List<Case> cases = caseRepository.findAll();
            return ResponseEntity.ok(cases);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get case by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE', 'CLIENT')")
    public ResponseEntity<Case> getCaseById(@PathVariable("id") Long id) {
        try {
            Optional<Case> caseData = caseRepository.findById(id);
            return caseData.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create new case
     */
    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE')")
    public ResponseEntity<?> createCase(@RequestBody Case caseData) {
        try {
            // Require client with username
            if (caseData.getClient() == null || caseData.getClient().getUser() == null
                    || caseData.getClient().getUser().getName() == null
                    || caseData.getClient().getUser().getName().isBlank()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("Client username is required to create a case");
            }

            // Auto-generate a unique case number if missing
            if (caseData.getCaseNumber() == null || caseData.getCaseNumber().isBlank()) {
                caseData.setCaseNumber(generateUniqueCaseNumber());
            } else {
                if (!caseData.getCaseNumber().matches("\\d{4,5}")) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body("Case number must be 4 to 5 digits");
                }
                if (caseRepository.existsByCaseNumber(caseData.getCaseNumber())) {
                    return ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("Case number already exists");
                }
            }

            Case savedCase = caseRepository.save(caseData);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedCase);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating case: " + e.getMessage());
        }
    }

    private String generateUniqueCaseNumber() {
        // Numeric 4-5 digit code (e.g., 48215)
        String candidate;
        int attempts = 0;
        do {
            int rand = ThreadLocalRandom.current().nextInt(1000, 100000); // 4 or 5 digits
            candidate = String.valueOf(rand);
            attempts++;
        } while (caseRepository.existsByCaseNumber(candidate) && attempts < 10);
        return candidate;
    }

    /**
     * Update case
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE')")
    public ResponseEntity<?> updateCase(@PathVariable("id") Long id, @RequestBody Case caseData) {
        try {
            Optional<Case> existingCase = caseRepository.findById(id);
            if (existingCase.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Case caseToUpdate = existingCase.get();
            
            // Update fields
            caseToUpdate.setCaseNumber(caseData.getCaseNumber());
            caseToUpdate.setCaseTitle(caseData.getCaseTitle());
            caseToUpdate.setCaseType(caseData.getCaseType());
            caseToUpdate.setClient(caseData.getClient());
            caseToUpdate.setAdvocate(caseData.getAdvocate());
            caseToUpdate.setCourtName(caseData.getCourtName());
            caseToUpdate.setCourtType(caseData.getCourtType());
            caseToUpdate.setFilingDate(caseData.getFilingDate());
            caseToUpdate.setStatus(caseData.getStatus());
            caseToUpdate.setPriority(caseData.getPriority());
            caseToUpdate.setDescription(caseData.getDescription());
            caseToUpdate.setOpposingParty(caseData.getOpposingParty());
            caseToUpdate.setJudgeName(caseData.getJudgeName());

            Case updatedCase = caseRepository.save(caseToUpdate);
            return ResponseEntity.ok(updatedCase);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating case: " + e.getMessage());
        }
    }

    /**
     * Delete case
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteCase(@PathVariable("id") Long id) {
        try {
            if (!caseRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            caseRepository.deleteById(id);
            return ResponseEntity.ok().body("Case deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting case: " + e.getMessage());
        }
    }

    /**
     * Get cases by status
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE')")
    public ResponseEntity<List<Case>> getCasesByStatus(@PathVariable("status") Case.CaseStatus status) {
        try {
            List<Case> cases = caseRepository.findByStatus(status);
            return ResponseEntity.ok(cases);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get cases by client
     */
    @GetMapping("/client/{clientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE', 'CLIENT')")
    public ResponseEntity<List<Case>> getCasesByClient(@PathVariable("clientId") Long clientId) {
        try {
            List<Case> cases = caseRepository.findByClient_ClientId(clientId);
            return ResponseEntity.ok(cases);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get cases by advocate
     */
    @GetMapping("/advocate/{advocateId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE')")
    public ResponseEntity<List<Case>> getCasesByAdvocate(@PathVariable("advocateId") Long advocateId) {
        try {
            List<Case> cases = caseRepository.findByAdvocate_AdvocateId(advocateId);
            return ResponseEntity.ok(cases);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get cases by case type
     */
    @GetMapping("/type/{caseType}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE')")
    public ResponseEntity<List<Case>> getCasesByCaseType(@PathVariable("caseType") Case.CaseType caseType) {
        try {
            List<Case> cases = caseRepository.findByCaseType(caseType);
            return ResponseEntity.ok(cases);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search cases by case number or title
     */
    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE')")
    public ResponseEntity<List<Case>> searchCases(@RequestParam("query") String query) {
        try {
            List<Case> cases = caseRepository.findByCaseNumberContainingIgnoreCaseOrCaseTitleContainingIgnoreCase(query, query);
            return ResponseEntity.ok(cases);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
