package com.legal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class LegalCaseManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(LegalCaseManagementApplication.class, args);
        System.out.println("⚖️ Smart Legal Case Management System Started Successfully!");
        System.out.println("🌐 Access at: http://localhost:8080");
    }
}
