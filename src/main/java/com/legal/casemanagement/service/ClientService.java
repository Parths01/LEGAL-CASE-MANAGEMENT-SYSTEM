package com.legal.casemanagement.service;

import com.legal.casemanagement.dto.ClientDtos.ClientDTO;
import com.legal.casemanagement.dto.ClientDtos.ClientListItem;
import com.legal.casemanagement.dto.ClientDtos.CreateClientRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ClientService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public ClientService(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public List<ClientListItem> getClients() {
        String sql = "SELECT c.client_id, u.name, u.email, c.client_type, c.company_name " +
                "FROM clients c JOIN users u ON c.user_id = u.user_id ORDER BY u.name";
        return jdbcTemplate.query(sql, (rs, rowNum) -> ClientListItem.builder()
                .clientId(rs.getLong("client_id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .clientType(rs.getString("client_type"))
                .companyName(rs.getString("company_name"))
                .build());
    }

    public ClientDTO getClientDetail(Long clientId) {
        String sql = "SELECT c.client_id, c.user_id, u.name, u.email, u.phone, c.address, " +
                "c.client_type, c.company_name, c.gstin, c.pan_number " +
                "FROM clients c JOIN users u ON c.user_id = u.user_id WHERE c.client_id = ?";
        
        List<ClientDTO> results = jdbcTemplate.query(sql, (rs, rowNum) -> ClientDTO.builder()
                .clientId(rs.getLong("client_id"))
                .userId(rs.getLong("user_id"))
                .name(rs.getString("name"))
                .email(rs.getString("email"))
                .phone(rs.getString("phone"))
                .address(rs.getString("address"))
                .clientType(rs.getString("client_type"))
                .companyName(rs.getString("company_name"))
                .gstin(rs.getString("gstin"))
                .panNumber(rs.getString("pan_number"))
                .build(), clientId);
        
        return results.isEmpty() ? null : results.get(0);
    }

    public ClientDTO createClient(CreateClientRequest request) {
        if (request == null || request.getEmail() == null || request.getEmail().isBlank()
                || request.getPassword() == null || request.getPassword().isBlank()
                || request.getName() == null || request.getName().isBlank()) {
            return null;
        }

        Integer exists = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE email = ?",
                Integer.class,
                request.getEmail()
        );
        if (exists != null && exists > 0) {
            return null;
        }

        SimpleJdbcInsert userInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("users")
                .usingGeneratedKeyColumns("user_id");

        Map<String, Object> userParams = new HashMap<>();
        userParams.put("name", request.getName());
        userParams.put("email", request.getEmail());
        userParams.put("password", passwordEncoder.encode(request.getPassword()));
        userParams.put("role", "CLIENT");
        userParams.put("phone", request.getPhone());
        userParams.put("address", request.getAddress());
        userParams.put("status", "ACTIVE");
        userParams.put("created_at", LocalDateTime.now());
        userParams.put("updated_at", LocalDateTime.now());

        Number userId = userInsert.executeAndReturnKey(userParams);

        SimpleJdbcInsert clientInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("clients")
                .usingGeneratedKeyColumns("client_id");

        Map<String, Object> clientParams = new HashMap<>();
        clientParams.put("user_id", userId.longValue());
        clientParams.put("address", request.getAddress());
        clientParams.put("company_name", request.getCompanyName());
        clientParams.put("gstin", request.getGstin());
        clientParams.put("pan_number", request.getPanNumber());
        clientParams.put("client_type", Optional.ofNullable(request.getClientType()).orElse("INDIVIDUAL"));

        Number clientId = clientInsert.executeAndReturnKey(clientParams);

        return ClientDTO.builder()
                .clientId(clientId.longValue())
                .userId(userId.longValue())
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .clientType(Optional.ofNullable(request.getClientType()).orElse("INDIVIDUAL"))
                .companyName(request.getCompanyName())
                .build();
    }
}
