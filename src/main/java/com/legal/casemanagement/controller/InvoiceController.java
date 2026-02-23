package com.legal.casemanagement.controller;

import com.legal.casemanagement.dto.InvoiceDtos.*;
import com.legal.casemanagement.service.InvoiceService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin(origins = "*", maxAge = 3600)
public class InvoiceController {

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @GetMapping
    public ResponseEntity<List<InvoiceListItem>> getInvoices(
            @RequestParam(required = false) Long caseId,
            @RequestParam(required = false) Long clientId) {
        return ResponseEntity.ok(invoiceService.getInvoices(caseId, clientId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getInvoiceDetail(@PathVariable Long id) {
        InvoiceDetail detail = invoiceService.getInvoiceDetail(id);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    @PostMapping
    public ResponseEntity<?> createInvoice(@RequestBody CreateInvoiceRequest request) {
        InvoiceResponse response = invoiceService.createInvoice(request);
        if (response == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Unable to create invoice. Check required fields."));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateInvoiceStatus(@PathVariable Long id,
            @RequestBody UpdateInvoiceStatusRequest request) {
        if (request == null || request.getStatus() == null) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Status is required"));
        }
        boolean updated = invoiceService.updateInvoiceStatus(id, request.getStatus());
        if (!updated) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new SuccessResponse("Invoice status updated to " + request.getStatus()));
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
