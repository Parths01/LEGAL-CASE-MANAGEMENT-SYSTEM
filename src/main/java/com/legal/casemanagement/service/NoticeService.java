package com.legal.casemanagement.service;

import com.legal.casemanagement.dto.NoticeDtos.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NoticeService {

    private final JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public NoticeService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<NoticeListItem> getNotices(Long caseId) {
        StringBuilder sql = new StringBuilder(
                "SELECT n.notice_id, c.case_number, c.case_title, n.notice_type, " +
                        "n.recipient_name, n.sent_date, n.status " +
                        "FROM legal_notices n " +
                        "JOIN cases c ON n.case_id = c.case_id ");

        if (caseId != null) {
            sql.append("WHERE n.case_id = ? ORDER BY n.generated_at DESC");
            return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapListItem(rs), caseId);
        }

        sql.append("ORDER BY n.generated_at DESC");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapListItem(rs));
    }

    public NoticeDetail getNoticeDetail(Long noticeId) {
        String sql = "SELECT n.notice_id, c.case_number, n.notice_type, n.recipient_name, " +
                "n.recipient_address, n.content, n.sent_date, n.status " +
                "FROM legal_notices n " +
                "JOIN cases c ON n.case_id = c.case_id " +
                "WHERE n.notice_id = ?";

        List<NoticeDetail> results = jdbcTemplate.query(sql, (rs, rowNum) -> NoticeDetail.builder()
                .noticeId(rs.getLong("notice_id"))
                .caseNumber(rs.getString("case_number"))
                .noticeType(rs.getString("notice_type"))
                .recipientName(rs.getString("recipient_name"))
                .recipientAddress(rs.getString("recipient_address"))
                .content(rs.getString("content"))
                .sentDate(formatDate(rs.getDate("sent_date")))
                .status(rs.getString("status"))
                .build(), noticeId);

        return results.isEmpty() ? null : results.get(0);
    }

    public NoticeResponse createNotice(CreateNoticeRequest request) {
        if (request == null || request.getRecipientName() == null || request.getContent() == null) {
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

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("legal_notices")
                .usingGeneratedKeyColumns("notice_id");

        Map<String, Object> params = new HashMap<>();
        params.put("case_id", caseId);
        params.put("notice_type", request.getNoticeType() != null ? request.getNoticeType() : "OTHER");
        params.put("recipient_name", request.getRecipientName());
        params.put("recipient_address", request.getRecipientAddress());
        params.put("content", request.getContent());
        params.put("sent_date", request.getSentDate());
        params.put("status", "DRAFT");

        Number key = insert.executeAndReturnKey(params);
        return NoticeResponse.builder()
                .noticeId(key.longValue())
                .message("Notice created successfully")
                .build();
    }

    public boolean updateNoticeStatus(Long noticeId, String status) {
        String sql = "UPDATE legal_notices SET status = ? WHERE notice_id = ?";
        int rows = jdbcTemplate.update(sql, status, noticeId);
        return rows > 0;
    }

    private NoticeListItem mapListItem(java.sql.ResultSet rs) throws java.sql.SQLException {
        return NoticeListItem.builder()
                .noticeId(rs.getLong("notice_id"))
                .caseNumber(rs.getString("case_number"))
                .caseTitle(rs.getString("case_title"))
                .noticeType(rs.getString("notice_type"))
                .recipientName(rs.getString("recipient_name"))
                .sentDate(formatDate(rs.getDate("sent_date")))
                .status(rs.getString("status"))
                .build();
    }

    private String formatDate(Date date) {
        if (date == null)
            return "";
        return DATE_FORMAT.format(date.toLocalDate());
    }
}
