package com.legal.casemanagement.service;

import com.legal.casemanagement.dto.TaskDtos.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TaskService {

    private final JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public TaskService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<TaskListItem> getTasks(Long caseId, Long assignedTo) {
        StringBuilder sql = new StringBuilder(
                "SELECT t.task_id, c.case_number, c.case_title, t.task_title, t.description, " +
                        "u.name AS assigned_to_name, t.due_date, t.priority, t.status " +
                        "FROM tasks t " +
                        "JOIN cases c ON t.case_id = c.case_id " +
                        "LEFT JOIN users u ON t.assigned_to = u.user_id ");

        if (caseId != null && assignedTo != null) {
            sql.append("WHERE t.case_id = ? AND t.assigned_to = ? ORDER BY t.due_date ASC");
            return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapItem(rs), caseId, assignedTo);
        } else if (caseId != null) {
            sql.append("WHERE t.case_id = ? ORDER BY t.due_date ASC");
            return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapItem(rs), caseId);
        } else if (assignedTo != null) {
            sql.append("WHERE t.assigned_to = ? ORDER BY t.due_date ASC");
            return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapItem(rs), assignedTo);
        }

        sql.append("ORDER BY t.due_date ASC");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapItem(rs));
    }

    public TaskResponse createTask(CreateTaskRequest request) {
        if (request == null || request.getTaskTitle() == null || request.getTaskTitle().isBlank()) {
            return null;
        }

        Long caseId = request.getCaseId();
        if (caseId == null && request.getCaseNumber() != null) {
            List<Long> ids = jdbcTemplate.query(
                    "SELECT case_id FROM cases WHERE case_number = ? LIMIT 1",
                    (rs, rowNum) -> rs.getLong("case_id"),
                    request.getCaseNumber());
            if (!ids.isEmpty())
                caseId = ids.get(0);
        }
        if (caseId == null)
            return null;

        Long createdBy = request.getCreatedBy();
        if (createdBy == null) {
            List<Long> adminIds = jdbcTemplate.query(
                    "SELECT user_id FROM users WHERE role = 'ADMIN' LIMIT 1",
                    (rs, rowNum) -> rs.getLong("user_id"));
            if (adminIds.isEmpty())
                return null;
            createdBy = adminIds.get(0);
        }

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("tasks")
                .usingGeneratedKeyColumns("task_id");

        Map<String, Object> params = new HashMap<>();
        params.put("case_id", caseId);
        params.put("assigned_to", request.getAssignedTo());
        params.put("task_title", request.getTaskTitle());
        params.put("description", request.getDescription());
        params.put("due_date", request.getDueDate());
        params.put("priority", request.getPriority() != null ? request.getPriority() : "MEDIUM");
        params.put("status", "PENDING");
        params.put("created_by", createdBy);

        Number key = insert.executeAndReturnKey(params);
        return TaskResponse.builder()
                .taskId(key.longValue())
                .message("Task created successfully")
                .build();
    }

    public boolean updateTaskStatus(Long taskId, String status) {
        String sql = "UPDATE tasks SET status = ?, updated_at = NOW() WHERE task_id = ?";
        int rows = jdbcTemplate.update(sql, status, taskId);
        return rows > 0;
    }

    private TaskListItem mapItem(java.sql.ResultSet rs) throws java.sql.SQLException {
        Timestamp due = rs.getTimestamp("due_date");
        String dueStr = "";
        boolean overdue = false;
        if (due != null) {
            dueStr = DATE_FORMAT.format(due.toLocalDateTime());
            overdue = due.toInstant().isBefore(Instant.now())
                    && !"COMPLETED".equalsIgnoreCase(rs.getString("status"))
                    && !"CANCELLED".equalsIgnoreCase(rs.getString("status"));
        }
        return TaskListItem.builder()
                .taskId(rs.getLong("task_id"))
                .caseNumber(rs.getString("case_number"))
                .caseTitle(rs.getString("case_title"))
                .taskTitle(rs.getString("task_title"))
                .description(rs.getString("description"))
                .assignedToName(rs.getString("assigned_to_name"))
                .dueDate(dueStr)
                .priority(rs.getString("priority"))
                .status(rs.getString("status"))
                .overdue(overdue)
                .build();
    }
}
