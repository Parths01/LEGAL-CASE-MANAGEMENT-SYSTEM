package com.legal.casemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClerkDashboardSummary {
    private Long totalCases;
    private Long documentsCount;
    private Long pendingInvoices;
    private Long hearingsThisWeek;
    private List<DashboardItem> documentRequests;
    private List<DashboardItem> reminders;
}
