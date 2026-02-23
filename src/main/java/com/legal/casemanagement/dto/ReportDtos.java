package com.legal.casemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class ReportDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ReportSummary {
        // Case stats
        private long totalCases;
        private long openCases;
        private long inProgressCases;
        private long closedCases;
        private long wonCases;
        private long lostCases;
        private long settledCases;

        // User stats
        private long totalUsers;
        private long totalAdvocates;
        private long totalClients;
        private long totalClerks;

        // Financial stats
        private long totalInvoices;
        private long paidInvoices;
        private long pendingInvoices;
        private long overdueInvoices;
        private BigDecimal totalRevenue;
        private BigDecimal pendingRevenue;

        // Hearing stats
        private long totalHearings;
        private long scheduledHearings;
        private long completedHearings;

        // Task stats
        private long totalTasks;
        private long pendingTasks;
        private long completedTasks;

        // Case type breakdown
        private Map<String, Long> casesByType;

        // Monthly case counts (last 6 months)
        private List<MonthlyCount> monthlyCases;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MonthlyCount {
        private String month;
        private long count;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CaseReportItem {
        private String caseNumber;
        private String caseTitle;
        private String caseType;
        private String clientName;
        private String advocateName;
        private String status;
        private String priority;
        private String filingDate;
    }
}
