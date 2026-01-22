package com.legal.controller;

import com.legal.entity.Client;
import com.legal.repository.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/clients")
@CrossOrigin(origins = "*", maxAge = 3600)
public class ClientController {

    @Autowired
    private ClientRepository clientRepository;

    /**
     * Get all clients
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE')")
    public ResponseEntity<List<Client>> getAllClients() {
        try {
            List<Client> clients = clientRepository.findAll();
            return ResponseEntity.ok(clients);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get client by ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE', 'CLIENT')")
    public ResponseEntity<Client> getClientById(@PathVariable("id") Long id) {
        try {
            Optional<Client> client = clientRepository.findById(id);
            return client.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Create new client
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createClient(@RequestBody Client client) {
        try {
            Client savedClient = clientRepository.save(client);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedClient);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error creating client: " + e.getMessage());
        }
    }

    /**
     * Update client
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CLIENT')")
    public ResponseEntity<?> updateClient(@PathVariable("id") Long id, @RequestBody Client client) {
        try {
            Optional<Client> existingClient = clientRepository.findById(id);
            if (existingClient.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Client clientToUpdate = existingClient.get();
            clientToUpdate.setAddress(client.getAddress());
            clientToUpdate.setCompanyName(client.getCompanyName());
            clientToUpdate.setGstin(client.getGstin());
            clientToUpdate.setPanNumber(client.getPanNumber());
            clientToUpdate.setClientType(client.getClientType());

            Client updatedClient = clientRepository.save(clientToUpdate);
            return ResponseEntity.ok(updatedClient);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating client: " + e.getMessage());
        }
    }

    /**
     * Delete client
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteClient(@PathVariable("id") Long id) {
        try {
            if (!clientRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }

            clientRepository.deleteById(id);
            return ResponseEntity.ok().body("Client deleted successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error deleting client: " + e.getMessage());
        }
    }

    /**
     * Get client by user ID
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'ADVOCATE', 'CLIENT')")
    public ResponseEntity<Client> getClientByUserId(@PathVariable("userId") Long userId) {
        try {
            Optional<Client> client = clientRepository.findByUser_UserId(userId);
            return client.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.notFound().build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
