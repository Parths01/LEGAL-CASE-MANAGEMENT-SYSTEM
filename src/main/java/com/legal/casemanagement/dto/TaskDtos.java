package com.legal.casemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class TaskDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaskListItem {
        private Long taskId;
        private String caseNumber;
        private String caseTitle;
        private String taskTitle;
        private String description;
        private String assignedToName;
        private String dueDate;
        private String priority;
        private String status;
        private boolean overdue;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateTaskRequest {
        private Long caseId;
        private String caseNumber;
        private Long assignedTo;
        private String taskTitle;
        private String description;
        private String dueDate;
        private String priority;
        private Long createdBy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateTaskStatusRequest {
        private String status; // PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TaskResponse {
        private Long taskId;
        private String message;
    }
}
