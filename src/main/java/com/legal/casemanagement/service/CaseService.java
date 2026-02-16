package com.legal.casemanagement.service;

import com.legal.casemanagement.dto.CaseDtos.AddNoteRequest;
import com.legal.casemanagement.dto.CaseDtos.CaseDetail;
import com.legal.casemanagement.dto.CaseDtos.CaseDocumentItem;
import com.legal.casemanagement.dto.CaseDtos.CaseHearingItem;
import com.legal.casemanagement.dto.CaseDtos.CaseListItem;
import com.legal.casemanagement.dto.CaseDtos.CaseNoteItem;
import com.legal.casemanagement.dto.CaseDtos.CaseTaskItem;
import com.legal.casemanagement.dto.CaseDtos.CaseTimelineItem;
import com.legal.casemanagement.dto.CaseDtos.UpdateCaseRequest;
import com.legal.casemanagement.dto.CaseDtos.UpdateTaskStatusRequest;
import com.legal.casemanagement.dto.CaseDtos.CreateCaseRequest;
import com.legal.casemanagement.dto.CaseDtos.CreateCaseResponse;
import com.legal.casemanagement.dto.CaseDtos.UploadDocumentRequest;
import com.legal.casemanagement.dto.CaseDtos.DocumentResponse;
import com.legal.casemanagement.dto.CaseDtos.ScheduleHearingRequest;
import com.legal.casemanagement.dto.CaseDtos.HearingResponse;
import com.legal.casemanagement.dto.CaseDtos.SendMessageRequest;
import com.legal.casemanagement.dto.CaseDtos.MessageResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

@Service
public class CaseService {

    private final JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public CaseService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<CaseListItem> getCases(String role, Long userId) {
        StringBuilder sql = new StringBuilder(
                "SELECT c.case_number, c.case_title, c.case_type, c.status, c.priority, " +
                        "cu.name AS client_name, au.name AS advocate_name, " +
                        "(SELECT MIN(h.hearing_date) FROM hearings h " +
                        " WHERE h.case_id = c.case_id AND h.status = 'SCHEDULED' AND h.hearing_date >= NOW()) AS next_hearing, " +
                        "(SELECT COUNT(*) FROM tasks t " +
                        " WHERE t.case_id = c.case_id AND t.status IN ('PENDING','IN_PROGRESS') AND t.due_date < NOW()) AS overdue_tasks " +
                        "FROM cases c " +
                        "JOIN clients cl ON c.client_id = cl.client_id " +
                        "JOIN users cu ON cl.user_id = cu.user_id " +
                        "JOIN advocates a ON c.advocate_id = a.advocate_id " +
                        "JOIN users au ON a.user_id = au.user_id "
        );

        List<Object> params = new ArrayList<>();
        String roleUpper = role == null ? "" : role.trim().toUpperCase(Locale.ROOT);
        if (userId != null && ("ADVOCATE".equals(roleUpper) || "CLIENT".equals(roleUpper))) {
            sql.append(" WHERE ");
            if ("ADVOCATE".equals(roleUpper)) {
                sql.append("a.user_id = ? ");
            } else {
                sql.append("cl.user_id = ? ");
            }
            params.add(userId);
        }
        sql.append(" ORDER BY c.created_at DESC");

        return jdbcTemplate.query(sql.toString(), params.toArray(), (rs, rowNum) -> CaseListItem.builder()
                .caseNumber(rs.getString("case_number"))
                .title(rs.getString("case_title"))
                .client(rs.getString("client_name"))
                .advocate(rs.getString("advocate_name"))
                .category(mapCaseType(rs.getString("case_type")))
                .status(mapCaseStatus(rs.getString("status")))
                .priority(mapPriority(rs.getString("priority")))
                .nextHearing(formatDate(rs.getTimestamp("next_hearing")))
                .overdueTasks(Optional.ofNullable(rs.getLong("overdue_tasks")).orElse(0L))
                .build());
    }

    // Overload for backward compatibility
    public CaseDetail getCaseDetail(@NonNull String caseNumber) {
        return getCaseDetail(caseNumber, null, null);
    }

    public CaseDetail getCaseDetail(@NonNull String caseNumber, String role, Long userId) {
        StringBuilder sqlBuilder = new StringBuilder(
                "SELECT c.case_id, c.case_number, c.case_title, c.case_type, c.status, c.priority, " +
                "c.filing_date, c.court_name, c.judge_name, c.description, c.opposing_party, " +
                "cu.name AS client_name, au.name AS advocate_name " +
                "FROM cases c " +
                "LEFT JOIN clients cl ON c.client_id = cl.client_id " +
                "LEFT JOIN users cu ON cl.user_id = cu.user_id " +
                "LEFT JOIN advocates a ON c.advocate_id = a.advocate_id " +
                "LEFT JOIN users au ON a.user_id = au.user_id " +
                "WHERE c.case_number = ?"
        );
        
        // Apply user-based access control - only for non-admin users
        String roleUpper = role == null ? "" : role.trim().toUpperCase(Locale.ROOT);
        System.out.println("DEBUG getCaseDetail - Role: '" + role + "', RoleUpper: '" + roleUpper + "', UserId: " + userId);
        
        // ADMIN users can see all cases, others are restricted
        if (!"ADMIN".equals(roleUpper) && userId != null) {
            System.out.println("DEBUG: Applying access restrictions for role: " + roleUpper);
            sqlBuilder.append(" AND (");
            if ("ADVOCATE".equals(roleUpper) || "CLERK".equals(roleUpper)) {
                // Advocates and clerks can only see cases assigned to them
                sqlBuilder.append("a.user_id = ").append(userId);
            } else if ("CLIENT".equals(roleUpper)) {
                // Clients can only see their own cases
                sqlBuilder.append("cu.user_id = ").append(userId);
            }
            sqlBuilder.append(")");
        } else {
            System.out.println("DEBUG: No restrictions applied for role: " + roleUpper);
        }
        
        String sql = sqlBuilder.toString();
        System.out.println("DEBUG SQL: " + sql);

        return jdbcTemplate.query(sql, rs -> {
            if (!rs.next()) {
                return null;
            }
            long caseId = rs.getLong("case_id");

            CaseDetail detail = CaseDetail.builder()
                    .caseNumber(rs.getString("case_number"))
                    .title(rs.getString("case_title"))
                    .category(mapCaseType(rs.getString("case_type")))
                    .status(mapCaseStatus(rs.getString("status")))
                    .priority(mapPriority(rs.getString("priority")))
                    .filingDate(formatDate(rs.getDate("filing_date")))
                    .court(nullToEmpty(rs.getString("court_name")))
                    .judge(nullToEmpty(rs.getString("judge_name")))
                    .description(nullToEmpty(rs.getString("description")))
                    .client(rs.getString("client_name"))
                    .advocate(rs.getString("advocate_name"))
                    .opposingParty(nullToEmpty(rs.getString("opposing_party")))
                    .opposingCounsel("")
                    .timeline(loadTimeline(caseId, rs.getDate("filing_date")))
                    .hearings(loadHearings(caseId, rs.getString("court_name")))
                    .documents(loadDocuments(caseId))
                    .tasks(loadTasks(caseId))
                    .notes(loadNotes(caseId))
                    .build();

            return detail;
        }, caseNumber);
    }

    public boolean updateCase(@NonNull String caseNumber, UpdateCaseRequest request) {
        String sql = "UPDATE cases SET status = COALESCE(?, status), priority = COALESCE(?, priority), " +
                "description = COALESCE(?, description), court_name = COALESCE(?, court_name), " +
                "judge_name = COALESCE(?, judge_name), updated_at = NOW() WHERE case_number = ?";

        String status = mapStatusToDb(request.getStatus());
        String priority = mapPriorityToDb(request.getPriority());

        int updated = jdbcTemplate.update(sql,
                status,
                priority,
                request.getDescription(),
                request.getCourt(),
                request.getJudge(),
                caseNumber
        );

        return updated > 0;
    }

    public CaseNoteItem addNote(@NonNull String caseNumber, AddNoteRequest request) {
        if (request == null || request.getText() == null || request.getText().isBlank()) {
            return null;
        }
        Long caseId = getCaseId(caseNumber);
        if (caseId == null) {
            return null;
        }

        Long userId = request.getUserId();
        if (userId == null) {
            userId = getFallbackUserId();
        }
        if (userId == null) {
            return null;
        }

        String insertSql = "INSERT INTO case_notes (case_id, created_by, note_text) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertSql, caseId, userId, request.getText());

        String authorName = request.getAuthorName();
        if (authorName == null || authorName.isBlank()) {
            authorName = jdbcTemplate.queryForObject("SELECT name FROM users WHERE user_id = ?", String.class, userId);
        }

        return CaseNoteItem.builder()
                .author(authorName)
                .date(DATE_FORMAT.format(LocalDate.now()))
                .text(request.getText())
                .build();
    }

    public boolean updateTaskStatus(@NonNull String caseNumber, Long taskId, UpdateTaskStatusRequest request) {
        Long caseId = getCaseId(caseNumber);
        if (caseId == null) {
            return false;
        }
        String newStatus = request.isCompleted() ? "COMPLETED" : "IN_PROGRESS";
        String sql = "UPDATE tasks SET status = ?, updated_at = NOW() WHERE task_id = ? AND case_id = ?";
        int updated = jdbcTemplate.update(sql, newStatus, taskId, caseId);
        return updated > 0;
    }

    private List<CaseTimelineItem> loadTimeline(Long caseId, Date filingDate) {
        List<CaseTimelineItem> items = new ArrayList<>();
        if (filingDate != null) {
            items.add(CaseTimelineItem.builder()
                    .date(formatDate(filingDate))
                    .title("Case Filed")
                    .desc("Case registered with the court")
                    .build());
        }

        String sql = "SELECT hearing_date, hearing_type, status FROM hearings WHERE case_id = ? ORDER BY hearing_date DESC LIMIT 6";
        jdbcTemplate.query(sql, (ResultSet rs) -> {
            while (rs.next()) {
                items.add(CaseTimelineItem.builder()
                        .date(formatDate(rs.getTimestamp("hearing_date")))
                        .title("Hearing " + mapHearingType(rs.getString("hearing_type")))
                        .desc("Status: " + mapHearingStatus(rs.getString("status")))
                        .build());
            }
        }, caseId);

        return items;
    }

    private List<CaseHearingItem> loadHearings(Long caseId, String courtName) {
        String sql = "SELECT hearing_date, hearing_type, courtroom FROM hearings WHERE case_id = ? ORDER BY hearing_date ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp hearingDate = rs.getTimestamp("hearing_date");
            return CaseHearingItem.builder()
                    .purpose(mapHearingType(rs.getString("hearing_type")))
                    .date(formatDate(hearingDate))
                    .time(formatTime(hearingDate))
                    .location(nullToEmpty(rs.getString("courtroom"), courtName))
                    .build();
        }, caseId);
    }

    private List<CaseDocumentItem> loadDocuments(Long caseId) {
        String sql = "SELECT document_name, document_type, file_path, file_size FROM documents WHERE case_id = ? ORDER BY uploaded_at DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String filePath = rs.getString("file_path");
            String inferredType = inferDocumentType(filePath);
            return CaseDocumentItem.builder()
                    .name(rs.getString("document_name"))
                    .type(inferredType)
                    .size(formatFileSize(rs.getLong("file_size")))
                    .build();
        }, caseId);
    }

    private List<CaseTaskItem> loadTasks(Long caseId) {
        String sql = "SELECT task_id, task_title, due_date, status FROM tasks WHERE case_id = ? ORDER BY due_date ASC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Timestamp due = rs.getTimestamp("due_date");
            boolean completed = "COMPLETED".equalsIgnoreCase(rs.getString("status"));
            boolean overdue = due != null && due.toInstant().isBefore(java.time.Instant.now()) && !completed;
            return CaseTaskItem.builder()
                    .id(rs.getLong("task_id"))
                    .title(rs.getString("task_title"))
                    .due(formatDate(due))
                    .completed(completed)
                    .overdue(overdue)
                    .build();
        }, caseId);
    }

    private List<CaseNoteItem> loadNotes(Long caseId) {
        String sql = "SELECT n.note_id, n.note_text, n.created_at, u.name AS author_name " +
                "FROM case_notes n JOIN users u ON n.created_by = u.user_id " +
                "WHERE n.case_id = ? ORDER BY n.created_at DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> CaseNoteItem.builder()
                .id(rs.getLong("note_id"))
                .author(rs.getString("author_name"))
                .date(formatDate(rs.getTimestamp("created_at")))
                .text(rs.getString("note_text"))
                .build(), caseId);
    }

    private Long getCaseId(String caseNumber) {
        List<Long> ids = jdbcTemplate.query("SELECT case_id FROM cases WHERE case_number = ?", (rs, rowNum) -> rs.getLong("case_id"), caseNumber);
        return ids.isEmpty() ? null : ids.get(0);
    }

    private Long getFallbackUserId() {
        List<Long> ids = jdbcTemplate.query("SELECT user_id FROM users ORDER BY user_id LIMIT 1", (rs, rowNum) -> rs.getLong("user_id"));
        return ids.isEmpty() ? null : ids.get(0);
    }

    private String formatDate(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return DATE_FORMAT.format(timestamp.toLocalDateTime());
    }

    private String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        return DATE_FORMAT.format(date.toLocalDate());
    }

    private String formatTime(Timestamp timestamp) {
        if (timestamp == null) {
            return "";
        }
        return TIME_FORMAT.format(timestamp.toLocalDateTime());
    }

    private String formatFileSize(long size) {
        if (size <= 0) {
            return "N/A";
        }
        if (size < 1024) {
            return size + " B";
        }
        double kb = size / 1024.0;
        if (kb < 1024) {
            return String.format(Locale.ROOT, "%.1f KB", kb);
        }
        double mb = kb / 1024.0;
        return String.format(Locale.ROOT, "%.1f MB", mb);
    }

    private String mapCaseType(String caseType) {
        if (caseType == null) {
            return "Other";
        }
        return toTitleCase(caseType.replace('_', ' '));
    }

    private String mapCaseStatus(String status) {
        if (status == null) {
            return "Pending";
        }
        return switch (status) {
            case "OPEN", "IN_PROGRESS" -> "Active";
            case "CLOSED", "WON", "LOST", "SETTLED" -> "Closed";
            default -> "Pending";
        };
    }

    private String mapPriority(String priority) {
        if (priority == null) {
            return "Medium";
        }
        return switch (priority) {
            case "LOW" -> "Low";
            case "HIGH", "URGENT" -> "High";
            default -> "Medium";
        };
    }

    private String mapStatusToDb(String status) {
        if (status == null) {
            return null;
        }
        return switch (status.trim().toUpperCase(Locale.ROOT)) {
            case "ACTIVE" -> "IN_PROGRESS";
            case "PENDING" -> "OPEN";
            case "ESCALATED" -> "IN_PROGRESS";
            case "CLOSED" -> "CLOSED";
            default -> null;
        };
    }

    private String mapPriorityToDb(String priority) {
        if (priority == null) {
            return null;
        }
        return switch (priority.trim().toUpperCase(Locale.ROOT)) {
            case "LOW" -> "LOW";
            case "HIGH" -> "HIGH";
            case "MEDIUM" -> "MEDIUM";
            case "URGENT" -> "URGENT";
            default -> null;
        };
    }

    private String mapHearingType(String hearingType) {
        if (hearingType == null) {
            return "Hearing";
        }
        return toTitleCase(hearingType.replace('_', ' '));
    }

    private String mapHearingStatus(String status) {
        if (status == null) {
            return "Scheduled";
        }
        return toTitleCase(status.replace('_', ' '));
    }

    private String toTitleCase(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        String[] parts = value.toLowerCase(Locale.ROOT).split("\\s+");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if (part.isEmpty()) {
                continue;
            }
            builder.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) {
                builder.append(part.substring(1));
            }
            if (i < parts.length - 1) {
                builder.append(" ");
            }
        }
        return builder.toString();
    }

    private String inferDocumentType(String filePath) {
        if (filePath == null) {
            return "doc";
        }
        String lower = filePath.toLowerCase(Locale.ROOT);
        if (lower.endsWith(".pdf")) {
            return "pdf";
        }
        if (lower.endsWith(".doc") || lower.endsWith(".docx")) {
            return "doc";
        }
        if (lower.endsWith(".zip") || lower.endsWith(".rar")) {
            return "archive";
        }
        return "doc";
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private String nullToEmpty(String primary, String fallback) {
        if (primary != null && !primary.isBlank()) {
            return primary;
        }
        return fallback == null ? "" : fallback;
    }

    public CreateCaseResponse createCase(CreateCaseRequest request) {
        if (request == null || request.getCaseTitle() == null || request.getClientId() == null 
                || request.getAdvocateUserId() == null) {
            return null;
        }

        String caseNumber = generateCaseNumber();
        SimpleJdbcInsert caseInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("cases")
                .usingGeneratedKeyColumns("case_id");

        Map<String, Object> params = new HashMap<>();
        params.put("case_number", caseNumber);
        params.put("case_title", request.getCaseTitle());
        params.put("case_type", request.getCaseType() != null ? request.getCaseType() : "OTHER");
        params.put("client_id", request.getClientId());
        params.put("advocate_id", getAdvocateIdByUserId(request.getAdvocateUserId()));
        params.put("court_name", request.getCourtName());
        params.put("court_type", request.getCourtType());
        params.put("filing_date", request.getFilingDate());
        params.put("priority", request.getPriority() != null ? request.getPriority() : "MEDIUM");
        params.put("description", request.getDescription());
        params.put("opposing_party", request.getOpposingParty());
        params.put("judge_name", request.getJudgeName());
        params.put("status", "OPEN");

        caseInsert.execute(params);

        return CreateCaseResponse.builder()
                .caseNumber(caseNumber)
                .message("Case created successfully")
                .build();
    }

    private String generateCaseNumber() {
        String prefix = "LC-2026-";
        Integer max = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(CAST(SUBSTRING(case_number, 9) AS UNSIGNED)), 0) FROM cases WHERE case_number LIKE 'LC-2026-%'",
                Integer.class
        );
        int nextNum = (max != null ? max : 0) + 1;
        return prefix + String.format("%03d", nextNum);
    }

    private Long getAdvocateIdByUserId(Long userId) {
        List<Long> ids = jdbcTemplate.query(
                "SELECT advocate_id FROM advocates WHERE user_id = ? LIMIT 1",
                (rs, rowNum) -> rs.getLong("advocate_id"),
                userId
        );
        return ids.isEmpty() ? 1L : ids.get(0);
    }

    public DocumentResponse uploadDocument(UploadDocumentRequest request, Long uploadedByUserId) {
        try {
            Long caseId = request.getCaseId();
            
            // If caseId is not provided but caseNumber is, resolve it
            if (caseId == null && request.getCaseNumber() != null) {
                List<Long> ids = jdbcTemplate.query(
                        "SELECT case_id FROM cases WHERE case_number = ? LIMIT 1",
                        (rs, rowNum) -> rs.getLong("case_id"),
                        request.getCaseNumber()
                );
                if (!ids.isEmpty()) {
                    caseId = ids.get(0);
                }
            }
            
            if (caseId == null) {
                return null;
            }
            
            SimpleJdbcInsert documentInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("documents")
                    .usingGeneratedKeyColumns("document_id");

            Map<String, Object> params = new HashMap<>();
            params.put("case_id", caseId);
            params.put("document_name", request.getDocumentName());
            params.put("document_type", request.getDocumentType() != null ? request.getDocumentType() : "OTHER");
            params.put("file_path", "/documents/" + System.currentTimeMillis() + "_" + request.getDocumentName());
            params.put("file_size", request.getFileData() != null ? request.getFileData().length() : 0);
            params.put("uploaded_by", uploadedByUserId);
            params.put("description", request.getDescription());

            Number key = documentInsert.executeAndReturnKey(params);
            return DocumentResponse.builder()
                    .documentId(key.longValue())
                    .documentName(request.getDocumentName())
                    .message("Document uploaded successfully")
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    public HearingResponse scheduleHearing(ScheduleHearingRequest request) {
        try {
            // Resolve caseNumber to caseId if caseNumber is provided
            Long caseId = request.getCaseId();
            if (caseId == null && request.getCaseNumber() != null) {
                String sql = "SELECT case_id FROM cases WHERE case_number = ?";
                caseId = jdbcTemplate.queryForObject(sql, Long.class, request.getCaseNumber());
            }
            
            SimpleJdbcInsert hearingInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("hearings")
                    .usingGeneratedKeyColumns("hearing_id");

            String hearingDateTime = request.getHearingDate() + " " + request.getHearingTime();
            Map<String, Object> params = new HashMap<>();
            params.put("case_id", caseId);
            params.put("hearing_date", hearingDateTime);
            params.put("courtroom", request.getCourtroom());
            params.put("hearing_type", request.getHearingType() != null ? request.getHearingType() : "OTHER");
            params.put("status", "SCHEDULED");
            params.put("remarks", request.getRemarks());

            Number key = hearingInsert.executeAndReturnKey(params);
            return HearingResponse.builder()
                    .hearingId(key.longValue())
                    .hearingDate(hearingDateTime)
                    .message("Hearing scheduled successfully")
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    public MessageResponse sendMessage(SendMessageRequest request, Long senderId) {
        try {
            SimpleJdbcInsert messageInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("messages")
                    .usingGeneratedKeyColumns("message_id");

            Map<String, Object> params = new HashMap<>();
            params.put("case_id", request.getCaseId());
            params.put("sender_id", senderId);
            params.put("recipient_id", request.getRecipientId());
            params.put("subject", request.getSubject());
            params.put("message_text", request.getMessageText());
            params.put("is_read", false);

            Number key = messageInsert.executeAndReturnKey(params);
            return MessageResponse.builder()
                    .messageId(key.longValue())
                    .message("Message sent successfully")
                    .build();
        } catch (Exception e) {
            return null;
        }
    }
}
