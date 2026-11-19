
package com.subscription.subscription_management.service;

import com.subscription.subscription_management.RegisterRequest;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class RegisterService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public RegisterService(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public String register(RegisterRequest request) {

        if (request.fullName() == null || request.fullName().isBlank()
                || request.email() == null || request.email().isBlank()
                || request.phone() == null || request.phone().isBlank()
                || request.password() == null || request.password().isBlank()) {
            throw new IllegalArgumentException("All fields are required");
        }

        String fullName = request.fullName().trim();
        String email = request.email().trim();
        String phone = request.phone().trim();

        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Enter a valid email address");
        }

        if (request.password().length() < 8) {
            throw new IllegalArgumentException(
                    "Password must contain at least 8 characters");
        }

        Integer existing = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM customers WHERE email = ?",
                Integer.class,
                email
        );

        if (existing != null && existing > 0) {
            throw new IllegalArgumentException(
                    "An account with this email already exists");
        }

        String passwordHash = passwordEncoder.encode(request.password());

        try {
            jdbcTemplate.update("""
                INSERT INTO customers
                (full_name, email, phone, password_hash, status)
                VALUES (?, ?, ?, ?, ?)
                """,
                    fullName,
                    email,
                    phone,
                    passwordHash,
                    "ACTIVE"
            );
        } catch (DuplicateKeyException e) {
            throw new IllegalArgumentException(
                    "An account with this email already exists");
        }

        return "Customer account created successfully";
    }
}

