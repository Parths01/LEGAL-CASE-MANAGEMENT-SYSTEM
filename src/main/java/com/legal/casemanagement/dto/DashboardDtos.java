package com.legal.casemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class DashboardDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdminDashboardSummary {
        private Long totalUsers;
        private Long totalCases;
        private Long activeCases;
        private Long totalClients;
        private Long totalAdvocates;
        private Long pendingTasks;
        private Integer activeCasesPercent;
        private List<DashboardHearing> upcomingHearings;
        private List<DashboardDeadline> deadlines;
        private List<DashboardItem> pendingTasksList;
        private List<DashboardItem> alerts;
        private List<DashboardCase> recentCases;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdvocateDashboardSummary {
        private Long totalCases;
        private Long activeCases;
        private Long hearingsToday;
        private Long totalClients;
        private Long pendingTasks;
        private List<DashboardHearing> upcomingHearings;
        private List<DashboardItem> tasks;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClientDashboardSummary {
        private Long totalCases;
        private Long activeCases;
        private Long documentsCount;
        private Long pendingInvoices;
        private List<DashboardItem> updates;
        private List<DashboardItem> reminders;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ClerkDashboardSummary {
        private Long totalCases;
        private Long documentsCount;
        private Long pendingInvoices;
        private Long hearingsThisWeek;
        private List<DashboardItem> documentRequests;
        private List<DashboardItem> reminders;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DashboardCase {
        private String caseNumber;
        private String clientName;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DashboardDeadline {
        private String title;
        private String dueDate;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DashboardHearing {
        private String caseNumber;
        private String courtName;
        private String hearingDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DashboardItem {
        private String label;
        private String status;
    }
}
