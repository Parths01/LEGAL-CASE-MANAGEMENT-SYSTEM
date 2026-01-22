package com.legal.repository;

import com.legal.entity.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    
    Optional<Client> findByUserUserId(Long userId);
    
    Optional<Client> findByUser_UserId(Long userId);
    
    List<Client> findByClientType(Client.ClientType clientType);
}
