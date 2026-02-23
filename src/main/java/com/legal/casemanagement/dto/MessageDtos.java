package com.legal.casemanagement.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class MessageDtos {

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MessageListItem {
        private Long messageId;
        private String caseNumber;
        private String senderName;
        private String recipientName;
        private String subject;
        private String preview;
        private boolean read;
        private String sentAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MessageDetail {
        private Long messageId;
        private String caseNumber;
        private String senderName;
        private String recipientName;
        private String subject;
        private String messageText;
        private boolean read;
        private String sentAt;
        private String readAt;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class MarkReadResponse {
        private Long messageId;
        private String message;
    }

    /** A contact the current user is allowed to chat with */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ContactDTO {
        private Long userId;
        private String name;
        private String role;
        private int unreadCount;
        private String lastMessage;
        private String lastMessageAt;
    }

    /** Payload for sending a new message */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class SendMessageRequest {
        private Long senderId;
        private Long recipientId;
        private String messageText;
        private Long caseId; // optional
        private String subject; // optional
    }

    /** A single message in a conversation thread */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ThreadMessage {
        private Long messageId;
        private Long senderId;
        private String senderName;
        private String messageText;
        private String sentAt;
        private boolean read;
    }
}
