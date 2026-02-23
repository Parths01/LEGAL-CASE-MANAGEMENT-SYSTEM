package com.legal.casemanagement.controller;

import com.legal.casemanagement.dto.ReportDtos.*;
import com.legal.casemanagement.service.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Comprehensive KPI summary for the reports dashboard page.
     */
    @GetMapping("/summary")
    public ResponseEntity<ReportSummary> getSummary() {
        return ResponseEntity.ok(reportService.getSummary());
    }

    /**
     * Case-level report list for table/export.
     */
    @GetMapping("/cases")
    public ResponseEntity<List<CaseReportItem>> getCaseReport() {
        return ResponseEntity.ok(reportService.getCaseReport());
    }
}
