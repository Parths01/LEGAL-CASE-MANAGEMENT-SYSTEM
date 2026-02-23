package com.legal.casemanagement.service;

import com.legal.casemanagement.dto.ReportDtos.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReportService {

    private final JdbcTemplate jdbcTemplate;

    public ReportService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public ReportSummary getSummary() {
        // Case stats
        long totalCases = count("SELECT COUNT(*) FROM cases");
        long openCases = count("SELECT COUNT(*) FROM cases WHERE status = 'OPEN'");
        long inProgressCases = count("SELECT COUNT(*) FROM cases WHERE status = 'IN_PROGRESS'");
        long closedCases = count("SELECT COUNT(*) FROM cases WHERE status = 'CLOSED'");
        long wonCases = count("SELECT COUNT(*) FROM cases WHERE status = 'WON'");
        long lostCases = count("SELECT COUNT(*) FROM cases WHERE status = 'LOST'");
        long settledCases = count("SELECT COUNT(*) FROM cases WHERE status = 'SETTLED'");

        // User stats
        long totalUsers = count("SELECT COUNT(*) FROM users");
        long totalAdvocates = count("SELECT COUNT(*) FROM users WHERE role = 'ADVOCATE'");
        long totalClients = count("SELECT COUNT(*) FROM users WHERE role = 'CLIENT'");
        long totalClerks = count("SELECT COUNT(*) FROM users WHERE role = 'CLERK'");

        // Invoice stats
        long totalInvoices = count("SELECT COUNT(*) FROM invoices");
        long paidInvoices = count("SELECT COUNT(*) FROM invoices WHERE status = 'PAID'");
        long pendingInvoices = count("SELECT COUNT(*) FROM invoices WHERE status IN ('DRAFT','SENT')");
        long overdueInvoices = count("SELECT COUNT(*) FROM invoices WHERE status = 'OVERDUE'");

        BigDecimal totalRevenue = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(total_amount),0) FROM invoices WHERE status = 'PAID'",
                BigDecimal.class);
        BigDecimal pendingRevenue = jdbcTemplate.queryForObject(
                "SELECT COALESCE(SUM(total_amount),0) FROM invoices WHERE status IN ('DRAFT','SENT','OVERDUE')",
                BigDecimal.class);

        // Hearing stats
        long totalHearings = count("SELECT COUNT(*) FROM hearings");
        long scheduledHearings = count("SELECT COUNT(*) FROM hearings WHERE status = 'SCHEDULED'");
        long completedHearings = count("SELECT COUNT(*) FROM hearings WHERE status = 'COMPLETED'");

        // Task stats
        long totalTasks = count("SELECT COUNT(*) FROM tasks");
        long pendingTasks = count("SELECT COUNT(*) FROM tasks WHERE status IN ('PENDING','IN_PROGRESS')");
        long completedTasks = count("SELECT COUNT(*) FROM tasks WHERE status = 'COMPLETED'");

        // Case type breakdown
        Map<String, Long> casesByType = new LinkedHashMap<>();
        jdbcTemplate.query(
                "SELECT case_type, COUNT(*) AS cnt FROM cases GROUP BY case_type ORDER BY cnt DESC",
                (RowCallbackHandler) rs -> casesByType.put(rs.getString("case_type"), rs.getLong("cnt")));

        // Monthly cases (last 6 months)
        List<MonthlyCount> monthlyCases = new ArrayList<>();
        jdbcTemplate.query(
                "SELECT DATE_FORMAT(created_at, '%Y-%m') AS month, COUNT(*) AS cnt " +
                        "FROM cases WHERE created_at >= DATE_SUB(NOW(), INTERVAL 6 MONTH) " +
                        "GROUP BY month ORDER BY month ASC",
                (RowCallbackHandler) rs -> monthlyCases.add(MonthlyCount.builder()
                        .month(rs.getString("month"))
                        .count(rs.getLong("cnt"))
                        .build()));

        return ReportSummary.builder()
                .totalCases(totalCases)
                .openCases(openCases)
                .inProgressCases(inProgressCases)
                .closedCases(closedCases)
                .wonCases(wonCases)
                .lostCases(lostCases)
                .settledCases(settledCases)
                .totalUsers(totalUsers)
                .totalAdvocates(totalAdvocates)
                .totalClients(totalClients)
                .totalClerks(totalClerks)
                .totalInvoices(totalInvoices)
                .paidInvoices(paidInvoices)
                .pendingInvoices(pendingInvoices)
                .overdueInvoices(overdueInvoices)
                .totalRevenue(totalRevenue != null ? totalRevenue : BigDecimal.ZERO)
                .pendingRevenue(pendingRevenue != null ? pendingRevenue : BigDecimal.ZERO)
                .totalHearings(totalHearings)
                .scheduledHearings(scheduledHearings)
                .completedHearings(completedHearings)
                .totalTasks(totalTasks)
                .pendingTasks(pendingTasks)
                .completedTasks(completedTasks)
                .casesByType(casesByType)
                .monthlyCases(monthlyCases)
                .build();
    }

    public List<CaseReportItem> getCaseReport() {
        String sql = "SELECT c.case_number, c.case_title, c.case_type, " +
                "cu.name AS client_name, au.name AS advocate_name, " +
                "c.status, c.priority, c.filing_date " +
                "FROM cases c " +
                "JOIN clients cl ON c.client_id = cl.client_id " +
                "JOIN users cu ON cl.user_id = cu.user_id " +
                "JOIN advocates a ON c.advocate_id = a.advocate_id " +
                "JOIN users au ON a.user_id = au.user_id " +
                "ORDER BY c.created_at DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> CaseReportItem.builder()
                .caseNumber(rs.getString("case_number"))
                .caseTitle(rs.getString("case_title"))
                .caseType(rs.getString("case_type"))
                .clientName(rs.getString("client_name"))
                .advocateName(rs.getString("advocate_name"))
                .status(rs.getString("status"))
                .priority(rs.getString("priority"))
                .filingDate(rs.getDate("filing_date") != null ? rs.getDate("filing_date").toString() : "")
                .build());
    }

    private long count(String sql) {
        Long val = jdbcTemplate.queryForObject(sql, Long.class);
        return val != null ? val : 0L;
    }
}
