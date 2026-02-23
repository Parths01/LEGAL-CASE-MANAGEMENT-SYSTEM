package com.legal.casemanagement.controller;

import com.legal.casemanagement.dto.CaseDtos.AddNoteRequest;
import com.legal.casemanagement.dto.CaseDtos.CaseDetail;
import com.legal.casemanagement.dto.CaseDtos.CaseListItem;
import com.legal.casemanagement.dto.CaseDtos.CaseNoteItem;
import com.legal.casemanagement.dto.CaseDtos.UpdateCaseRequest;
import com.legal.casemanagement.dto.CaseDtos.UpdateTaskStatusRequest;
import com.legal.casemanagement.dto.CaseDtos.CreateCaseRequest;
import com.legal.casemanagement.dto.CaseDtos.CreateCaseResponse;
import com.legal.casemanagement.dto.CaseDtos.AdvocateOption;
import com.legal.casemanagement.dto.CaseDtos.UploadDocumentRequest;
import com.legal.casemanagement.dto.CaseDtos.DocumentResponse;
import com.legal.casemanagement.dto.CaseDtos.ScheduleHearingRequest;
import com.legal.casemanagement.dto.CaseDtos.HearingResponse;
import com.legal.casemanagement.dto.CaseDtos.SendMessageRequest;
import com.legal.casemanagement.dto.CaseDtos.MessageResponse;
import com.legal.casemanagement.service.CaseService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cases")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CaseController {

    private final CaseService caseService;

    public CaseController(CaseService caseService) {
        this.caseService = caseService;
    }

    @GetMapping
    public ResponseEntity<List<CaseListItem>> getCases(@RequestParam(required = false) String role,
            @RequestParam(required = false) Long userId) {
        return ResponseEntity.ok(caseService.getCases(role, userId));
    }

    @GetMapping("/advocates")
    public ResponseEntity<List<AdvocateOption>> getAssignableAdvocates() {
        return ResponseEntity.ok(caseService.getAssignableAdvocates());
    }

    @PostMapping
    public ResponseEntity<?> createCase(@RequestBody CreateCaseRequest request) {
        CreateCaseResponse created = caseService.createCase(request);
        if (created == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Unable to create case. Check required fields."));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{caseNumber}")
    public ResponseEntity<CaseDetail> getCaseDetail(@PathVariable String caseNumber,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) Long userId) {
        if (caseNumber == null || caseNumber.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        CaseDetail detail = caseService.getCaseDetail(caseNumber, role, userId);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    @PutMapping("/{caseNumber}")
    public ResponseEntity<?> updateCase(@PathVariable String caseNumber, @RequestBody UpdateCaseRequest request) {
        if (caseNumber == null || caseNumber.isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Case number required"));
        }
        boolean updated = caseService.updateCase(caseNumber, request);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Case not found"));
        }
        return ResponseEntity.ok(new SuccessResponse("Case updated successfully"));
    }

    @PostMapping("/{caseNumber}/notes")
    public ResponseEntity<?> addNote(@PathVariable String caseNumber, @RequestBody AddNoteRequest request) {
        if (caseNumber == null || caseNumber.isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Case number required"));
        }
        CaseNoteItem note = caseService.addNote(caseNumber, request);
        if (note == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ErrorResponse("Unable to add note"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    @PatchMapping("/{caseNumber}/tasks/{taskId}/status")
    public ResponseEntity<?> updateTaskStatus(@PathVariable String caseNumber,
            @PathVariable Long taskId,
            @RequestBody UpdateTaskStatusRequest request) {
        if (caseNumber == null || caseNumber.isBlank()) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Case number required"));
        }
        boolean updated = caseService.updateTaskStatus(caseNumber, taskId, request);
        if (!updated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse("Task not found"));
        }
        return ResponseEntity.ok(new SuccessResponse("Task status updated"));
    }

    @PostMapping("/documents/upload")
    public ResponseEntity<?> uploadDocument(@RequestBody UploadDocumentRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null)
            userId = 1L; // Default user for demo
        DocumentResponse response = caseService.uploadDocument(request, userId);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Unable to upload document"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/hearings/schedule")
    public ResponseEntity<?> scheduleHearing(@RequestBody ScheduleHearingRequest request) {
        HearingResponse response = caseService.scheduleHearing(request);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Unable to schedule hearing"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/messages/send")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        if (userId == null)
            userId = 1L; // Default user for demo
        MessageResponse response = caseService.sendMessage(request, userId);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Unable to send message"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/advocate/{advocateUserId}")
    public ResponseEntity<List<CaseListItem>> getCasesByAdvocate(@PathVariable Long advocateUserId) {
        return ResponseEntity.ok(caseService.getCases("ADVOCATE", advocateUserId));
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
