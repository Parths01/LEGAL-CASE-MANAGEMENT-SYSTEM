package com.legal.casemanagement.service;

import com.legal.casemanagement.dto.HearingDtos.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
public class HearingService {

    private final JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public HearingService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<HearingListItem> getHearings(Long caseId, String status) {
        StringBuilder sql = new StringBuilder(
                "SELECT h.hearing_id, c.case_number, c.case_title, h.hearing_date, " +
                        "h.hearing_type, h.courtroom, h.status, h.remarks " +
                        "FROM hearings h " +
                        "JOIN cases c ON h.case_id = c.case_id ");

        if (caseId != null && status != null) {
            sql.append("WHERE h.case_id = ? AND h.status = ? ORDER BY h.hearing_date ASC");
            return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapItem(rs), caseId, status);
        } else if (caseId != null) {
            sql.append("WHERE h.case_id = ? ORDER BY h.hearing_date ASC");
            return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapItem(rs), caseId);
        } else if (status != null) {
            sql.append("WHERE h.status = ? ORDER BY h.hearing_date ASC");
            return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapItem(rs), status);
        }

        sql.append("ORDER BY h.hearing_date DESC");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapItem(rs));
    }

    public HearingUpdateResponse updateHearingStatus(Long hearingId, UpdateHearingStatusRequest request) {
        if (request == null || request.getStatus() == null) {
            return null;
        }

        String sql = "UPDATE hearings SET status = ?, remarks = COALESCE(?, remarks), " +
                "next_hearing_date = ?, updated_at = NOW() WHERE hearing_id = ?";
        int rows = jdbcTemplate.update(sql,
                request.getStatus(),
                request.getRemarks(),
                request.getNextHearingDate(),
                hearingId);

        if (rows == 0)
            return null;

        return HearingUpdateResponse.builder()
                .hearingId(hearingId)
                .message("Hearing status updated to " + request.getStatus())
                .build();
    }

    private HearingListItem mapItem(java.sql.ResultSet rs) throws java.sql.SQLException {
        Timestamp hearingDate = rs.getTimestamp("hearing_date");
        String dateStr = "";
        String timeStr = "";
        if (hearingDate != null) {
            dateStr = DATE_FORMAT.format(hearingDate.toLocalDateTime());
            timeStr = TIME_FORMAT.format(hearingDate.toLocalDateTime());
        }
        return HearingListItem.builder()
                .hearingId(rs.getLong("hearing_id"))
                .caseNumber(rs.getString("case_number"))
                .caseTitle(rs.getString("case_title"))
                .hearingDate(dateStr)
                .hearingTime(timeStr)
                .hearingType(rs.getString("hearing_type"))
                .courtroom(rs.getString("courtroom"))
                .status(rs.getString("status"))
                .remarks(rs.getString("remarks"))
                .build();
    }
}
