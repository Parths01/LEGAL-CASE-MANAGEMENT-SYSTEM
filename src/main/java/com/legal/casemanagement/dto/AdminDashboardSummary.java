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
public class AdminDashboardSummary {
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
