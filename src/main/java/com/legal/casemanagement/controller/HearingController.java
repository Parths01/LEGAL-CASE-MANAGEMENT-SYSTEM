package com.legal.casemanagement.controller;

import com.legal.casemanagement.dto.HearingDtos.*;
import com.legal.casemanagement.service.HearingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hearings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class HearingController {

    private final HearingService hearingService;

    public HearingController(HearingService hearingService) {
        this.hearingService = hearingService;
    }

    @GetMapping
    public ResponseEntity<List<HearingListItem>> getHearings(
            @RequestParam(required = false) Long caseId,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(hearingService.getHearings(caseId, status));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateHearingStatus(@PathVariable Long id,
            @RequestBody UpdateHearingStatusRequest request) {
        HearingUpdateResponse response = hearingService.updateHearingStatus(id, request);
        if (response == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Unable to update hearing status"));
        }
        return ResponseEntity.ok(response);
    }

    static class ErrorResponse {
        public String message;

        public ErrorResponse(String message) {
            this.message = message;
        }
    }
}
