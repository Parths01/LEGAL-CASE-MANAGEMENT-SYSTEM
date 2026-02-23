package com.legal.casemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class NoticeDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoticeListItem {
        private Long noticeId;
        private String caseNumber;
        private String caseTitle;
        private String noticeType;
        private String recipientName;
        private String sentDate;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoticeDetail {
        private Long noticeId;
        private String caseNumber;
        private String noticeType;
        private String recipientName;
        private String recipientAddress;
        private String content;
        private String sentDate;
        private String status;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateNoticeRequest {
        private Long caseId;
        private String caseNumber;
        private String noticeType;
        private String recipientName;
        private String recipientAddress;
        private String content;
        private String sentDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateNoticeStatusRequest {
        private String status; // SENT, ACKNOWLEDGED, RESPONDED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class NoticeResponse {
        private Long noticeId;
        private String message;
    }
}
