package com.legal.casemanagement.controller;

import com.legal.casemanagement.dto.NoticeDtos.*;
import com.legal.casemanagement.service.NoticeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notices")
@CrossOrigin(origins = "*", maxAge = 3600)
public class NoticeController {

    private final NoticeService noticeService;

    public NoticeController(NoticeService noticeService) {
        this.noticeService = noticeService;
    }

    @GetMapping
    public ResponseEntity<List<NoticeListItem>> getNotices(
            @RequestParam(required = false) Long caseId) {
        return ResponseEntity.ok(noticeService.getNotices(caseId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getNoticeDetail(@PathVariable Long id) {
        NoticeDetail detail = noticeService.getNoticeDetail(id);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    @PostMapping
    public ResponseEntity<?> createNotice(@RequestBody CreateNoticeRequest request) {
        NoticeResponse response = noticeService.createNotice(request);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Unable to create notice. Check required fields."));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateNoticeStatus(@PathVariable Long id,
            @RequestBody UpdateNoticeStatusRequest request) {
        if (request == null || request.getStatus() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Status is required"));
        }
        boolean updated = noticeService.updateNoticeStatus(id, request.getStatus());
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new SuccessResponse("Notice status updated to " + request.getStatus()));
    }

    static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }

    static class SuccessResponse {
        public String message;

        public SuccessResponse(String message) {
            this.message = message;
        }
    }
}
