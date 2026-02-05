package com.legal.casemanagement.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> {})
            .authorizeHttpRequests(authorize -> authorize
                // Allow public access to index page
                .requestMatchers("/", "/index.html").permitAll()
                // Allow public access to dashboard pages (role check done on frontend)
                .requestMatchers("/admin-dashboard.html", "/advocate-dashboard.html", "/client-dashboard.html", "/clerk-dashboard.html").permitAll()
                // Allow public access to user management page
                .requestMatchers("/user-management.html").permitAll()
                // Allow public access to static resources
                .requestMatchers("/assets/**", "/css/**", "/js/**", "/images/**").permitAll()
                // Allow public access to login page
                .requestMatchers("/login", "/login.html").permitAll()
                // Allow public access to API login endpoint
                .requestMatchers("/api/auth/**").permitAll()
                // Allow public access to dashboard summary API
                .requestMatchers("/api/dashboard/**").permitAll()
                // Allow public access to user management API (role check done on frontend)
                .requestMatchers("/api/users/**").permitAll()
                // Require authentication for all other requests
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }
}
