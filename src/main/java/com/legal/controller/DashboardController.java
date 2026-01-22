package com.legal.controller;

import com.legal.dto.ApiResponse;
import com.legal.entity.Case;
import com.legal.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "*")
public class DashboardController {

    @Autowired
    private CaseRepository caseRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private HearingRepository hearingRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/stats")
    public ResponseEntity<?> getAdminStats() {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCases", caseRepository.count());
            stats.put("totalAdvocates", userRepository.countByRole(com.legal.entity.User.UserRole.ADVOCATE));
            stats.put("totalClients", userRepository.countByRole(com.legal.entity.User.UserRole.CLIENT));
            stats.put("pendingHearings", hearingRepository.countByStatus(com.legal.entity.Hearing.HearingStatus.SCHEDULED));
            stats.put("totalRevenue", invoiceRepository.calculateTotalRevenue());
            stats.put("pendingRevenue", invoiceRepository.calculatePendingRevenue());
            stats.put("monthlyRevenue", invoiceRepository.fetchMonthlyRevenue());
            stats.put("caseStatusDistribution", caseRepository.fetchStatusDistribution());
            
            return ResponseEntity.ok(new ApiResponse(true, "Stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve stats: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('ADVOCATE')")
    @GetMapping("/advocate/stats")
    public ResponseEntity<?> getAdvocateStats(@RequestParam Long advocateId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCases", caseRepository.countByAdvocate(advocateId));
            stats.put("activeCases", caseRepository.findByAdvocateAndStatus(advocateId, Case.CaseStatus.IN_PROGRESS).size());
            stats.put("todayHearings", hearingRepository.findTodaysHearingsByAdvocate(
                advocateId, 
                java.time.LocalDateTime.now().withHour(0).withMinute(0),
                java.time.LocalDateTime.now().withHour(23).withMinute(59)
            ).size());
            
            return ResponseEntity.ok(new ApiResponse(true, "Stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve stats: " + e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/client/stats")
    public ResponseEntity<?> getClientStats(@RequestParam Long clientId) {
        try {
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalCases", caseRepository.findByClientClientId(clientId).size());
            stats.put("pendingInvoices", invoiceRepository.findByClientClientId(clientId).stream()
                    .filter(inv -> inv.getStatus() == com.legal.entity.Invoice.InvoiceStatus.SENT).count());
            
            return ResponseEntity.ok(new ApiResponse(true, "Stats retrieved successfully", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, "Failed to retrieve stats: " + e.getMessage()));
        }
    }
}
