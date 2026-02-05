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
public class AdvocateDashboardSummary {
    private Long totalCases;
    private Long activeCases;
    private Long hearingsToday;
    private Long totalClients;
    private Long pendingTasks;
    private List<DashboardHearing> upcomingHearings;
    private List<DashboardItem> tasks;
}
