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
public class ClientDashboardSummary {
    private Long totalCases;
    private Long activeCases;
    private Long documentsCount;
    private Long pendingInvoices;
    private List<DashboardItem> updates;
    private List<DashboardItem> reminders;
}
