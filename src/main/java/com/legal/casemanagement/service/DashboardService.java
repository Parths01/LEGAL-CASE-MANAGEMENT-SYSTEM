package com.legal.casemanagement.service;

import com.legal.casemanagement.dto.AdminDashboardSummary;
import com.legal.casemanagement.dto.AdvocateDashboardSummary;
import com.legal.casemanagement.dto.ClientDashboardSummary;
import com.legal.casemanagement.dto.ClerkDashboardSummary;
import com.legal.casemanagement.dto.DashboardCase;
import com.legal.casemanagement.dto.DashboardDeadline;
import com.legal.casemanagement.dto.DashboardHearing;
import com.legal.casemanagement.dto.DashboardItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class DashboardService {

    private final JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public DashboardService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public AdminDashboardSummary getAdminSummary() {
        long totalUsers = queryForLong("SELECT COUNT(*) FROM users");
        long totalCases = queryForLong("SELECT COUNT(*) FROM cases");
        long activeCases = queryForLong("SELECT COUNT(*) FROM cases WHERE status IN ('OPEN','IN_PROGRESS')");
        long totalClients = queryForLong("SELECT COUNT(*) FROM clients");
        long totalAdvocates = queryForLong("SELECT COUNT(*) FROM advocates");
        long pendingInvoices = queryForLong("SELECT COUNT(*) FROM invoices WHERE status IN ('DRAFT','SENT','OVERDUE')");
        long draftNotices = queryForLong("SELECT COUNT(*) FROM legal_notices WHERE status = 'DRAFT'");
        long pendingTasks = pendingInvoices + draftNotices;

        int activeCasesPercent = totalCases == 0 ? 0 : (int) Math.round((activeCases * 100.0) / totalCases);

        List<DashboardHearing> upcomingHearings = jdbcTemplate.query(
                "SELECT c.case_number, c.court_name, h.hearing_date " +
                        "FROM hearings h JOIN cases c ON c.case_id = h.case_id " +
                        "WHERE h.status = 'SCHEDULED' AND h.hearing_date >= NOW() " +
                        "ORDER BY h.hearing_date ASC LIMIT 3",
                (rs, rowNum) -> DashboardHearing.builder()
                        .caseNumber(rs.getString("case_number"))
                        .courtName(rs.getString("court_name") == null ? "Court" : rs.getString("court_name"))
                        .hearingDate(formatDateTime(rs.getTimestamp("hearing_date")))
                        .build()
        );

        List<DashboardDeadline> deadlines = jdbcTemplate.query(
                "SELECT invoice_number, due_date, status FROM invoices " +
                        "WHERE due_date IS NOT NULL AND status IN ('SENT','OVERDUE') " +
                        "ORDER BY due_date ASC LIMIT 4",
                (rs, rowNum) -> DashboardDeadline.builder()
                        .title("Invoice #" + rs.getString("invoice_number"))
                        .dueDate(formatDate(rs.getDate("due_date")))
                        .status(mapStatus(rs.getString("status")))
                        .build()
        );

        long overdueInvoices = queryForLong("SELECT COUNT(*) FROM invoices WHERE status = 'OVERDUE'");
        long hearingsToday = queryForLong("SELECT COUNT(*) FROM hearings WHERE DATE(hearing_date) = CURDATE() AND status = 'SCHEDULED'");

        List<DashboardItem> pendingTasksList = new ArrayList<>();
        pendingTasksList.add(DashboardItem.builder().label("Approve invoices (" + pendingInvoices + ")").status(pendingInvoices > 0 ? "warn" : "success").build());
        pendingTasksList.add(DashboardItem.builder().label("Draft notices (" + draftNotices + ")").status(draftNotices > 0 ? "info" : "success").build());
        pendingTasksList.add(DashboardItem.builder().label("Scheduled hearings (" + hearingsToday + ")").status(hearingsToday > 0 ? "info" : "success").build());

        List<DashboardItem> alerts = new ArrayList<>();
        alerts.add(DashboardItem.builder().label("Overdue invoices (" + overdueInvoices + ")").status(overdueInvoices > 0 ? "warn" : "success").build());
        alerts.add(DashboardItem.builder().label("Hearings today (" + hearingsToday + ")").status(hearingsToday > 0 ? "info" : "success").build());
        alerts.add(DashboardItem.builder().label("Draft notices (" + draftNotices + ")").status(draftNotices > 0 ? "warn" : "success").build());

        List<DashboardCase> recentCases = jdbcTemplate.query(
                "SELECT c.case_number, u.name, c.status " +
                        "FROM cases c JOIN clients cl ON c.client_id = cl.client_id " +
                        "JOIN users u ON cl.user_id = u.user_id " +
                        "ORDER BY c.created_at DESC LIMIT 4",
                (rs, rowNum) -> DashboardCase.builder()
                        .caseNumber(rs.getString("case_number"))
                        .clientName(rs.getString("name"))
                        .status(mapCaseStatus(rs.getString("status")))
                        .build()
        );

        return AdminDashboardSummary.builder()
                .totalUsers(totalUsers)
                .totalCases(totalCases)
                .activeCases(activeCases)
                .totalClients(totalClients)
                .totalAdvocates(totalAdvocates)
                .pendingTasks(pendingTasks)
                .activeCasesPercent(activeCasesPercent)
                .upcomingHearings(upcomingHearings)
                .deadlines(deadlines)
                .pendingTasksList(pendingTasksList)
                .alerts(alerts)
                .recentCases(recentCases)
                .build();
    }

    public AdvocateDashboardSummary getAdvocateSummary() {
        long totalCases = queryForLong("SELECT COUNT(*) FROM cases");
        long activeCases = queryForLong("SELECT COUNT(*) FROM cases WHERE status IN ('OPEN','IN_PROGRESS')");
        long hearingsToday = queryForLong("SELECT COUNT(*) FROM hearings WHERE DATE(hearing_date) = CURDATE() AND status = 'SCHEDULED'");
        long totalClients = queryForLong("SELECT COUNT(*) FROM clients");
        long pendingTasks = queryForLong("SELECT COUNT(*) FROM documents WHERE uploaded_at >= DATE_SUB(NOW(), INTERVAL 1 DAY)");

        List<DashboardHearing> upcomingHearings = jdbcTemplate.query(
                "SELECT c.case_number, c.court_name, h.hearing_date " +
                        "FROM hearings h JOIN cases c ON c.case_id = h.case_id " +
                        "WHERE h.status = 'SCHEDULED' AND h.hearing_date >= NOW() " +
                        "ORDER BY h.hearing_date ASC LIMIT 3",
                (rs, rowNum) -> DashboardHearing.builder()
                        .caseNumber(rs.getString("case_number"))
                        .courtName(rs.getString("court_name") == null ? "Court" : rs.getString("court_name"))
                        .hearingDate(formatDateTime(rs.getTimestamp("hearing_date")))
                        .build()
        );

        List<DashboardItem> tasks = new ArrayList<>();
        tasks.add(DashboardItem.builder().label("Hearings today (" + hearingsToday + ")").status(hearingsToday > 0 ? "info" : "success").build());
        tasks.add(DashboardItem.builder().label("Active cases (" + activeCases + ")").status(activeCases > 0 ? "warn" : "success").build());
        tasks.add(DashboardItem.builder().label("New documents (" + pendingTasks + ")").status(pendingTasks > 0 ? "info" : "success").build());

        return AdvocateDashboardSummary.builder()
                .totalCases(totalCases)
                .activeCases(activeCases)
                .hearingsToday(hearingsToday)
                .totalClients(totalClients)
                .pendingTasks(pendingTasks)
                .upcomingHearings(upcomingHearings)
                .tasks(tasks)
                .build();
    }

    public ClientDashboardSummary getClientSummary() {
        long totalCases = queryForLong("SELECT COUNT(*) FROM cases");
        long activeCases = queryForLong("SELECT COUNT(*) FROM cases WHERE status IN ('OPEN','IN_PROGRESS')");
        long documentsCount = queryForLong("SELECT COUNT(*) FROM documents");
        long pendingInvoices = queryForLong("SELECT COUNT(*) FROM invoices WHERE status IN ('SENT','OVERDUE')");

        List<DashboardItem> updates = new ArrayList<>();
        updates.add(DashboardItem.builder().label("Active cases (" + activeCases + ")").status(activeCases > 0 ? "info" : "success").build());
        updates.add(DashboardItem.builder().label("Documents uploaded (" + documentsCount + ")").status(documentsCount > 0 ? "info" : "success").build());

        List<DashboardItem> reminders = new ArrayList<>();
        reminders.add(DashboardItem.builder().label("Pending invoices (" + pendingInvoices + ")").status(pendingInvoices > 0 ? "warn" : "success").build());
        reminders.add(DashboardItem.builder().label("Upcoming hearings").status("info").build());

        return ClientDashboardSummary.builder()
                .totalCases(totalCases)
                .activeCases(activeCases)
                .documentsCount(documentsCount)
                .pendingInvoices(pendingInvoices)
                .updates(updates)
                .reminders(reminders)
                .build();
    }

    public ClerkDashboardSummary getClerkSummary() {
        long totalCases = queryForLong("SELECT COUNT(*) FROM cases");
        long documentsCount = queryForLong("SELECT COUNT(*) FROM documents");
        long pendingInvoices = queryForLong("SELECT COUNT(*) FROM invoices WHERE status IN ('DRAFT','SENT','OVERDUE')");
        long hearingsThisWeek = queryForLong("SELECT COUNT(*) FROM hearings WHERE hearing_date BETWEEN NOW() AND DATE_ADD(NOW(), INTERVAL 7 DAY) AND status = 'SCHEDULED'");

        List<DashboardItem> documentRequests = new ArrayList<>();
        documentRequests.add(DashboardItem.builder().label("Pending invoices (" + pendingInvoices + ")").status(pendingInvoices > 0 ? "warn" : "success").build());
        documentRequests.add(DashboardItem.builder().label("Documents on file (" + documentsCount + ")").status(documentsCount > 0 ? "info" : "success").build());

        List<DashboardItem> reminders = new ArrayList<>();
        reminders.add(DashboardItem.builder().label("Hearings this week (" + hearingsThisWeek + ")").status(hearingsThisWeek > 0 ? "info" : "success").build());
        reminders.add(DashboardItem.builder().label("Case registry update").status("info").build());

        return ClerkDashboardSummary.builder()
                .totalCases(totalCases)
                .documentsCount(documentsCount)
                .pendingInvoices(pendingInvoices)
                .hearingsThisWeek(hearingsThisWeek)
                .documentRequests(documentRequests)
                .reminders(reminders)
                .build();
    }

    private long queryForLong(@NonNull String sql) {
        Long value = jdbcTemplate.queryForObject(sql, Long.class);
        return value == null ? 0L : value;
    }

    private String formatDateTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        LocalDateTime dateTime = timestamp.toLocalDateTime();
        return DATE_TIME_FORMAT.format(dateTime);
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        LocalDate localDate = date.toLocalDate();
        return DATE_FORMAT.format(localDate);
    }

    private String mapStatus(String status) {
        if (status == null) {
            return "info";
        }
        return switch (status) {
            case "OVERDUE" -> "warn";
            case "PAID" -> "success";
            default -> "info";
        };
    }

    private String mapCaseStatus(String status) {
        if (status == null) {
            return "info";
        }
        return switch (status) {
            case "OPEN" -> "warn";
            case "CLOSED" -> "success";
            case "IN_PROGRESS" -> "info";
            default -> "info";
        };
    }
}
