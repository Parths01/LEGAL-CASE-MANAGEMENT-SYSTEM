package com.legal.casemanagement.service;

import com.legal.casemanagement.dto.MessageDtos.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class MessageService {

    private final JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    public MessageService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // ─────────────────── Inbox / Sent ───────────────────────

    public List<MessageListItem> getInbox(Long userId) {
        String sql = "SELECT m.message_id, c.case_number, su.name AS sender_name, " +
                "ru.name AS recipient_name, m.subject, m.message_text, m.is_read, m.sent_at " +
                "FROM messages m " +
                "LEFT JOIN cases c ON m.case_id = c.case_id " +
                "JOIN users su ON m.sender_id = su.user_id " +
                "JOIN users ru ON m.recipient_id = ru.user_id " +
                "WHERE m.recipient_id = ? " +
                "ORDER BY m.sent_at DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String text = rs.getString("message_text");
            String preview = text != null && text.length() > 80 ? text.substring(0, 80) + "…" : text;
            return MessageListItem.builder()
                    .messageId(rs.getLong("message_id"))
                    .caseNumber(rs.getString("case_number"))
                    .senderName(rs.getString("sender_name"))
                    .recipientName(rs.getString("recipient_name"))
                    .subject(rs.getString("subject"))
                    .preview(preview)
                    .read(rs.getBoolean("is_read"))
                    .sentAt(formatTs(rs.getTimestamp("sent_at")))
                    .build();
        }, userId);
    }

    public List<MessageListItem> getSent(Long userId) {
        String sql = "SELECT m.message_id, c.case_number, su.name AS sender_name, " +
                "ru.name AS recipient_name, m.subject, m.message_text, m.is_read, m.sent_at " +
                "FROM messages m " +
                "LEFT JOIN cases c ON m.case_id = c.case_id " +
                "JOIN users su ON m.sender_id = su.user_id " +
                "JOIN users ru ON m.recipient_id = ru.user_id " +
                "WHERE m.sender_id = ? " +
                "ORDER BY m.sent_at DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            String text = rs.getString("message_text");
            String preview = text != null && text.length() > 80 ? text.substring(0, 80) + "…" : text;
            return MessageListItem.builder()
                    .messageId(rs.getLong("message_id"))
                    .caseNumber(rs.getString("case_number"))
                    .senderName(rs.getString("sender_name"))
                    .recipientName(rs.getString("recipient_name"))
                    .subject(rs.getString("subject"))
                    .preview(preview)
                    .read(rs.getBoolean("is_read"))
                    .sentAt(formatTs(rs.getTimestamp("sent_at")))
                    .build();
        }, userId);
    }

    public boolean markAsRead(Long messageId) {
        String sql = "UPDATE messages SET is_read = TRUE, read_at = NOW() WHERE message_id = ?";
        return jdbcTemplate.update(sql, messageId) > 0;
    }

    // ─────────────────── Chat Contacts ──────────────────────

    /**
     * Returns the list of users the given userId/role is allowed to chat with,
     * along with unread message count and last message preview per contact.
     *
     * Rules:
     * CLIENT → Admin + Advocates assigned to their cases
     * ADVOCATE → Admin + Clients whose cases they handle + all Clerks
     * CLERK → Admin + all Advocates
     * ADMIN → everyone else (all active users)
     */
    public List<ContactDTO> getContacts(Long userId, String role) {
        String sql;

        switch (role.toUpperCase()) {
            case "CLIENT":
                // Admin + Advocates assigned to this client's cases
                sql = "SELECT u.user_id, u.name, u.role, " +
                        "SUM(CASE WHEN m.recipient_id = ? AND m.is_read = FALSE THEN 1 ELSE 0 END) AS unread_count, " +
                        "MAX(m.sent_at) AS last_at, " +
                        "SUBSTRING_INDEX(GROUP_CONCAT(m.message_text ORDER BY m.sent_at DESC SEPARATOR '|||'), '|||', 1) AS last_msg "
                        +
                        "FROM ( " +
                        "  SELECT u2.user_id, u2.name, u2.role FROM users u2 WHERE u2.role = 'ADMIN' AND u2.status = 'ACTIVE' "
                        +
                        "  UNION " +
                        "  SELECT u3.user_id, u3.name, u3.role FROM users u3 " +
                        "  JOIN advocates a ON a.user_id = u3.user_id " +
                        "  JOIN cases c ON c.advocate_id = a.advocate_id " +
                        "  JOIN clients cl ON cl.client_id = c.client_id " +
                        "  WHERE cl.user_id = ? AND u3.status = 'ACTIVE' " +
                        ") u " +
                        "LEFT JOIN messages m ON (m.sender_id = u.user_id AND m.recipient_id = ?) " +
                        "                     OR (m.sender_id = ? AND m.recipient_id = u.user_id) " +
                        "GROUP BY u.user_id, u.name, u.role " +
                        "ORDER BY last_at DESC, u.name";
                return jdbcTemplate.query(sql, (rs, rowNum) -> buildContact(rs),
                        userId, userId, userId, userId);

            case "ADVOCATE":
                // Admin + Clients whose cases this advocate handles + all Clerks
                sql = "SELECT u.user_id, u.name, u.role, " +
                        "SUM(CASE WHEN m.recipient_id = ? AND m.is_read = FALSE THEN 1 ELSE 0 END) AS unread_count, " +
                        "MAX(m.sent_at) AS last_at, " +
                        "SUBSTRING_INDEX(GROUP_CONCAT(m.message_text ORDER BY m.sent_at DESC SEPARATOR '|||'), '|||', 1) AS last_msg "
                        +
                        "FROM ( " +
                        "  SELECT u2.user_id, u2.name, u2.role FROM users u2 WHERE u2.role = 'ADMIN' AND u2.status = 'ACTIVE' "
                        +
                        "  UNION " +
                        "  SELECT u3.user_id, u3.name, u3.role FROM users u3 " +
                        "  JOIN clients cl ON cl.user_id = u3.user_id " +
                        "  JOIN cases c ON c.client_id = cl.client_id " +
                        "  JOIN advocates a ON a.advocate_id = c.advocate_id " +
                        "  WHERE a.user_id = ? AND u3.status = 'ACTIVE' " +
                        "  UNION " +
                        "  SELECT u4.user_id, u4.name, u4.role FROM users u4 WHERE u4.role = 'CLERK' AND u4.status = 'ACTIVE' "
                        +
                        ") u " +
                        "LEFT JOIN messages m ON (m.sender_id = u.user_id AND m.recipient_id = ?) " +
                        "                     OR (m.sender_id = ? AND m.recipient_id = u.user_id) " +
                        "GROUP BY u.user_id, u.name, u.role " +
                        "ORDER BY last_at DESC, u.name";
                return jdbcTemplate.query(sql, (rs, rowNum) -> buildContact(rs),
                        userId, userId, userId, userId);

            case "CLERK":
                // Admin + all Advocates
                sql = "SELECT u.user_id, u.name, u.role, " +
                        "SUM(CASE WHEN m.recipient_id = ? AND m.is_read = FALSE THEN 1 ELSE 0 END) AS unread_count, " +
                        "MAX(m.sent_at) AS last_at, " +
                        "SUBSTRING_INDEX(GROUP_CONCAT(m.message_text ORDER BY m.sent_at DESC SEPARATOR '|||'), '|||', 1) AS last_msg "
                        +
                        "FROM users u " +
                        "LEFT JOIN messages m ON (m.sender_id = u.user_id AND m.recipient_id = ?) " +
                        "                     OR (m.sender_id = ? AND m.recipient_id = u.user_id) " +
                        "WHERE u.role IN ('ADMIN', 'ADVOCATE') AND u.status = 'ACTIVE' AND u.user_id != ? " +
                        "GROUP BY u.user_id, u.name, u.role " +
                        "ORDER BY last_at DESC, u.name";
                return jdbcTemplate.query(sql, (rs, rowNum) -> buildContact(rs),
                        userId, userId, userId, userId);

            case "ADMIN":
            default:
                // Everyone except self
                sql = "SELECT u.user_id, u.name, u.role, " +
                        "SUM(CASE WHEN m.recipient_id = ? AND m.is_read = FALSE THEN 1 ELSE 0 END) AS unread_count, " +
                        "MAX(m.sent_at) AS last_at, " +
                        "SUBSTRING_INDEX(GROUP_CONCAT(m.message_text ORDER BY m.sent_at DESC SEPARATOR '|||'), '|||', 1) AS last_msg "
                        +
                        "FROM users u " +
                        "LEFT JOIN messages m ON (m.sender_id = u.user_id AND m.recipient_id = ?) " +
                        "                     OR (m.sender_id = ? AND m.recipient_id = u.user_id) " +
                        "WHERE u.user_id != ? AND u.status = 'ACTIVE' " +
                        "GROUP BY u.user_id, u.name, u.role " +
                        "ORDER BY last_at DESC, u.name";
                return jdbcTemplate.query(sql, (rs, rowNum) -> buildContact(rs),
                        userId, userId, userId, userId);
        }
    }

    private ContactDTO buildContact(java.sql.ResultSet rs) throws java.sql.SQLException {
        String lastMsg = rs.getString("last_msg");
        if (lastMsg != null && lastMsg.length() > 60)
            lastMsg = lastMsg.substring(0, 60) + "…";
        return ContactDTO.builder()
                .userId(rs.getLong("user_id"))
                .name(rs.getString("name"))
                .role(rs.getString("role"))
                .unreadCount(rs.getInt("unread_count"))
                .lastMessage(lastMsg)
                .lastMessageAt(formatTs(rs.getTimestamp("last_at")))
                .build();
    }

    // ─────────────────── Send Message ───────────────────────

    public Long sendMessage(SendMessageRequest req) {
        String sql = "INSERT INTO messages (case_id, sender_id, recipient_id, subject, message_text, is_read, sent_at) "
                +
                "VALUES (?, ?, ?, ?, ?, FALSE, NOW())";
        jdbcTemplate.update(sql,
                req.getCaseId(), // nullable
                req.getSenderId(),
                req.getRecipientId(),
                req.getSubject(),
                req.getMessageText());
        return jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
    }

    // ─────────────────── Conversation Thread ────────────────

    /**
     * Returns full conversation (both directions) between two users, oldest first.
     * Also marks all unread messages sent TO userId as read.
     */
    public List<ThreadMessage> getThread(Long userId, Long otherId) {
        // Mark incoming messages as read
        jdbcTemplate.update(
                "UPDATE messages SET is_read = TRUE, read_at = NOW() " +
                        "WHERE sender_id = ? AND recipient_id = ? AND is_read = FALSE",
                otherId, userId);

        String sql = "SELECT m.message_id, m.sender_id, u.name AS sender_name, " +
                "m.message_text, m.sent_at, m.is_read " +
                "FROM messages m " +
                "JOIN users u ON u.user_id = m.sender_id " +
                "WHERE (m.sender_id = ? AND m.recipient_id = ?) " +
                "   OR (m.sender_id = ? AND m.recipient_id = ?) " +
                "ORDER BY m.sent_at ASC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> ThreadMessage.builder()
                .messageId(rs.getLong("message_id"))
                .senderId(rs.getLong("sender_id"))
                .senderName(rs.getString("sender_name"))
                .messageText(rs.getString("message_text"))
                .sentAt(formatTs(rs.getTimestamp("sent_at")))
                .read(rs.getBoolean("is_read"))
                .build(),
                userId, otherId, otherId, userId);
    }

    // ─────────────────── Utility ────────────────────────────

    private String formatTs(Timestamp ts) {
        if (ts == null)
            return "";
        return DT_FORMAT.format(ts.toLocalDateTime());
    }
}
