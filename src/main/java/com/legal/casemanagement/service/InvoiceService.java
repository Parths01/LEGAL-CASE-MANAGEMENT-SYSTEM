package com.legal.casemanagement.service;

import com.legal.casemanagement.dto.InvoiceDtos.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class InvoiceService {

    private final JdbcTemplate jdbcTemplate;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public InvoiceService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<InvoiceListItem> getInvoices(Long caseId, Long clientId) {
        StringBuilder sql = new StringBuilder(
                "SELECT i.invoice_id, i.invoice_number, c.case_number, u.name AS client_name, " +
                        "i.invoice_date, i.due_date, i.amount, i.total_amount, i.status " +
                        "FROM invoices i " +
                        "JOIN cases c ON i.case_id = c.case_id " +
                        "JOIN clients cl ON i.client_id = cl.client_id " +
                        "JOIN users u ON cl.user_id = u.user_id ");

        if (caseId != null) {
            sql.append("WHERE i.case_id = ? ");
            return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapListItem(rs), caseId);
        } else if (clientId != null) {
            sql.append("WHERE i.client_id = ? ");
            return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapListItem(rs), clientId);
        }

        sql.append("ORDER BY i.invoice_date DESC");
        return jdbcTemplate.query(sql.toString(), (rs, rowNum) -> mapListItem(rs));
    }

    public InvoiceDetail getInvoiceDetail(Long invoiceId) {
        String sql = "SELECT i.invoice_id, i.invoice_number, c.case_number, c.case_title, " +
                "u.name AS client_name, i.invoice_date, i.due_date, i.amount, i.tax, " +
                "i.discount, i.total_amount, i.status, i.notes " +
                "FROM invoices i " +
                "JOIN cases c ON i.case_id = c.case_id " +
                "JOIN clients cl ON i.client_id = cl.client_id " +
                "JOIN users u ON cl.user_id = u.user_id " +
                "WHERE i.invoice_id = ?";

        List<InvoiceDetail> results = jdbcTemplate.query(sql, (rs, rowNum) -> InvoiceDetail.builder()
                .invoiceId(rs.getLong("invoice_id"))
                .invoiceNumber(rs.getString("invoice_number"))
                .caseNumber(rs.getString("case_number"))
                .caseTitle(rs.getString("case_title"))
                .clientName(rs.getString("client_name"))
                .invoiceDate(formatDate(rs.getDate("invoice_date")))
                .dueDate(formatDate(rs.getDate("due_date")))
                .amount(rs.getBigDecimal("amount"))
                .tax(rs.getBigDecimal("tax"))
                .discount(rs.getBigDecimal("discount"))
                .totalAmount(rs.getBigDecimal("total_amount"))
                .status(rs.getString("status"))
                .notes(rs.getString("notes"))
                .build(), invoiceId);

        return results.isEmpty() ? null : results.get(0);
    }

    public InvoiceResponse createInvoice(CreateInvoiceRequest request) {
        if (request == null || request.getAmount() == null) {
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

        Long clientId = jdbcTemplate.queryForObject(
                "SELECT client_id FROM cases WHERE case_id = ?", Long.class, caseId);
        if (clientId == null)
            return null;

        String invoiceNumber = generateInvoiceNumber();
        BigDecimal tax = request.getTax() != null ? request.getTax() : BigDecimal.ZERO;
        BigDecimal discount = request.getDiscount() != null ? request.getDiscount() : BigDecimal.ZERO;
        BigDecimal total = request.getAmount().add(tax).subtract(discount);

        SimpleJdbcInsert insert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("invoices")
                .usingGeneratedKeyColumns("invoice_id");

        Map<String, Object> params = new HashMap<>();
        params.put("invoice_number", invoiceNumber);
        params.put("case_id", caseId);
        params.put("client_id", clientId);
        params.put("invoice_date",
                request.getInvoiceDate() != null ? request.getInvoiceDate() : LocalDate.now().toString());
        params.put("due_date", request.getDueDate());
        params.put("amount", request.getAmount());
        params.put("tax", tax);
        params.put("discount", discount);
        params.put("total_amount", total);
        params.put("status", "DRAFT");
        params.put("notes", request.getNotes());

        Number key = insert.executeAndReturnKey(params);
        return InvoiceResponse.builder()
                .invoiceId(key.longValue())
                .invoiceNumber(invoiceNumber)
                .message("Invoice created successfully")
                .build();
    }

    public boolean updateInvoiceStatus(Long invoiceId, String status) {
        String sql = "UPDATE invoices SET status = ?, updated_at = NOW() WHERE invoice_id = ?";
        int rows = jdbcTemplate.update(sql, status, invoiceId);
        return rows > 0;
    }

    private String generateInvoiceNumber() {
        String year = String.valueOf(LocalDate.now().getYear());
        String prefix = "INV-" + year + "-";
        int suffixStart = prefix.length() + 1;
        Integer max = jdbcTemplate.queryForObject(
                "SELECT COALESCE(MAX(CAST(SUBSTRING(invoice_number, " + suffixStart + ") AS UNSIGNED)), 0) " +
                        "FROM invoices WHERE invoice_number LIKE ?",
                Integer.class,
                prefix + "%");
        int next = (max != null ? max : 0) + 1;
        return prefix + String.format("%04d", next);
    }

    private InvoiceListItem mapListItem(java.sql.ResultSet rs) throws java.sql.SQLException {
        return InvoiceListItem.builder()
                .invoiceId(rs.getLong("invoice_id"))
                .invoiceNumber(rs.getString("invoice_number"))
                .caseNumber(rs.getString("case_number"))
                .clientName(rs.getString("client_name"))
                .invoiceDate(formatDate(rs.getDate("invoice_date")))
                .dueDate(formatDate(rs.getDate("due_date")))
                .amount(rs.getBigDecimal("amount"))
                .totalAmount(rs.getBigDecimal("total_amount"))
                .status(rs.getString("status"))
                .build();
    }

    private String formatDate(Date date) {
        if (date == null)
            return "";
        return DATE_FORMAT.format(date.toLocalDate());
    }
}
