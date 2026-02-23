package com.legal.casemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class HearingDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HearingListItem {
        private Long hearingId;
        private String caseNumber;
        private String caseTitle;
        private String hearingDate;
        private String hearingTime;
        private String hearingType;
        private String courtroom;
        private String status;
        private String remarks;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateHearingStatusRequest {
        private String status; // COMPLETED, ADJOURNED, CANCELLED
        private String remarks;
        private String nextHearingDate;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HearingUpdateResponse {
        private Long hearingId;
        private String message;
    }
}
