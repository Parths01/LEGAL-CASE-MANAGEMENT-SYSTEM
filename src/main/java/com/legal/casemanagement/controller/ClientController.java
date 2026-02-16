package com.legal.casemanagement.controller;

import com.legal.casemanagement.dto.ClientDtos.ClientDTO;
import com.legal.casemanagement.dto.ClientDtos.ClientListItem;
import com.legal.casemanagement.dto.ClientDtos.CreateClientRequest;
import com.legal.casemanagement.service.ClientService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @GetMapping
    public ResponseEntity<List<ClientListItem>> getClients() {
        return ResponseEntity.ok(clientService.getClients());
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<ClientDTO> getClientDetail(@PathVariable Long clientId) {
        ClientDTO detail = clientService.getClientDetail(clientId);
        if (detail == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(detail);
    }

    @PostMapping
    public ResponseEntity<?> createClient(@RequestBody CreateClientRequest request) {
        ClientDTO created = clientService.createClient(request);
        if (created == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ErrorResponse("Unable to create client. Check email and required fields."));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    static class ErrorResponse {
        public String message;
        public ErrorResponse(String message) { this.message = message; }
    }
}
