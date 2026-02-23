package com.legal.casemanagement.controller;

import com.legal.casemanagement.dto.MessageDtos.*;
import com.legal.casemanagement.service.MessageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*", maxAge = 3600)
public class MessageController {

    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    // ── Existing endpoints ────────────────────────────────

    @GetMapping("/inbox")
    public ResponseEntity<List<MessageListItem>> getInbox(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(messageService.getInbox(userId));
    }

    @GetMapping("/sent")
    public ResponseEntity<List<MessageListItem>> getSent(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(messageService.getSent(userId));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        boolean updated = messageService.markAsRead(id);
        if (!updated)
            return ResponseEntity.notFound().build();
        return ResponseEntity.ok(MarkReadResponse.builder()
                .messageId(id).message("Message marked as read").build());
    }

    // ── New chat endpoints ────────────────────────────────

    /**
     * Returns the list of contacts the user is allowed to chat with
     * (role-filtered).
     * GET /api/messages/contacts?userId=1001&role=ADMIN
     */
    @GetMapping("/contacts")
    public ResponseEntity<List<ContactDTO>> getContacts(
            @RequestParam("userId") Long userId,
            @RequestParam("role") String role) {
        return ResponseEntity.ok(messageService.getContacts(userId, role));
    }

    /**
     * Returns the full conversation thread between two users (oldest → newest).
     * Also auto-marks incoming messages as read.
     * GET /api/messages/thread?userId=1001&otherId=1004
     */
    @GetMapping("/thread")
    public ResponseEntity<List<ThreadMessage>> getThread(
            @RequestParam("userId") Long userId,
            @RequestParam("otherId") Long otherId) {
        return ResponseEntity.ok(messageService.getThread(userId, otherId));
    }

    /**
     * Sends a new message.
     * POST /api/messages/send
     * Body: { senderId, recipientId, messageText, caseId?, subject? }
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageRequest request) {
        if (request.getSenderId() == null || request.getRecipientId() == null
                || request.getMessageText() == null || request.getMessageText().isBlank()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "senderId, recipientId and messageText are required"));
        }
        Long messageId = messageService.sendMessage(request);
        return ResponseEntity.ok(Map.of("messageId", messageId, "status", "sent"));
    }
}
