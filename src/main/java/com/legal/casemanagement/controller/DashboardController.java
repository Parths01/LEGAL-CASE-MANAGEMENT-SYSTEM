package com.legal.casemanagement.controller;

import com.legal.casemanagement.dto.AdminDashboardSummary;
import com.legal.casemanagement.dto.AdvocateDashboardSummary;
import com.legal.casemanagement.dto.ClientDashboardSummary;
import com.legal.casemanagement.dto.ClerkDashboardSummary;
import com.legal.casemanagement.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin/summary")
    public ResponseEntity<AdminDashboardSummary> getAdminSummary() {
        return ResponseEntity.ok(dashboardService.getAdminSummary());
    }

    @GetMapping("/advocate/summary")
    public ResponseEntity<AdvocateDashboardSummary> getAdvocateSummary() {
        return ResponseEntity.ok(dashboardService.getAdvocateSummary());
    }

    @GetMapping("/client/summary")
    public ResponseEntity<ClientDashboardSummary> getClientSummary() {
        return ResponseEntity.ok(dashboardService.getClientSummary());
    }

    @GetMapping("/clerk/summary")
    public ResponseEntity<ClerkDashboardSummary> getClerkSummary() {
        return ResponseEntity.ok(dashboardService.getClerkSummary());
    }
}
