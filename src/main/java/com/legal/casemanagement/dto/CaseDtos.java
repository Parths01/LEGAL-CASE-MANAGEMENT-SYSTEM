package com.legal.casemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

public class CaseDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CaseListItem {
        private String caseNumber;
        private String title;
        private String client;
        private String advocate;
        private String category;
        private String status;
        private String priority;
        private String nextHearing;
        private Long overdueTasks;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CaseTimelineItem {
        private String date;
        private String title;
        private String desc;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CaseHearingItem {
        private String purpose;
        private String date;
        private String time;
        private String location;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CaseDocumentItem {
        private String name;
        private String type;
        private String size;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CaseTaskItem {
        private Long id;
        private String title;
        private String due;
        private boolean completed;
        private boolean overdue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CaseNoteItem {
        private Long id;
        private String author;
        private String date;
        private String text;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CaseDetail {
        private String caseNumber;
        private String title;
        private String status;
        private String priority;
        private String category;
        private String filingDate;
        private String court;
        private String judge;
        private String description;
        private String client;
        private String advocate;
        private String opposingParty;
        private String opposingCounsel;
        private List<CaseTimelineItem> timeline;
        private List<CaseHearingItem> hearings;
        private List<CaseDocumentItem> documents;
        private List<CaseTaskItem> tasks;
        private List<CaseNoteItem> notes;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateCaseRequest {
        private String status;
        private String priority;
        private String description;
        private String court;
        private String judge;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AddNoteRequest {
        private String text;
        private Long userId;
        private String authorName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTaskStatusRequest {
        private boolean completed;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateCaseRequest {
        private String caseTitle;
        private String caseType;
        private Long clientId;
        private Long advocateUserId;
        private String courtName;
        private String courtType;
        private String filingDate;
        private String priority;
        private String description;
        private String opposingParty;
        private String judgeName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateCaseResponse {
        private String caseNumber;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class AdvocateOption {
        private Long advocateId;
        private Long advocateUserId;
        private String name;
        private String specialization;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadDocumentRequest {
        private Long caseId;
        private String caseNumber;
        private String documentName;
        private String documentType;
        private String description;
        private String fileData; // base64 encoded file data
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class DocumentResponse {
        private Long documentId;
        private String documentName;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ScheduleHearingRequest {
        private Long caseId;
        private String caseNumber;
        private String hearingDate;
        private String hearingTime;
        private String courtroom;
        private String hearingType;
        private String remarks;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HearingResponse {
        private Long hearingId;
        private String hearingDate;
        private String message;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SendMessageRequest {
        private Long caseId;
        private Long recipientId;
        private String subject;
        private String messageText;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MessageResponse {
        private Long messageId;
        private String message;
    }
}
