package com.legal.casemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class InvoiceDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InvoiceListItem {
        private Long invoiceId;
        private String invoiceNumber;
        private String caseNumber;
        private String clientName;
        private String invoiceDate;
        private String dueDate;
        private BigDecimal amount;
        private BigDecimal totalAmount;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InvoiceDetail {
        private Long invoiceId;
        private String invoiceNumber;
        private String caseNumber;
        private String caseTitle;
        private String clientName;
        private String invoiceDate;
        private String dueDate;
        private BigDecimal amount;
        private BigDecimal tax;
        private BigDecimal discount;
        private BigDecimal totalAmount;
        private String status;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateInvoiceRequest {
        private Long caseId;
        private String caseNumber;
        private String invoiceDate;
        private String dueDate;
        private BigDecimal amount;
        private BigDecimal tax;
        private BigDecimal discount;
        private String notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateInvoiceStatusRequest {
        private String status; // PAID, OVERDUE, CANCELLED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class InvoiceResponse {
        private Long invoiceId;
        private String invoiceNumber;
        private String message;
    }
}
