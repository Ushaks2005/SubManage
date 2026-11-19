
package com.subscription.subscription_management.service;

import com.subscription.subscription_management.AuthRequest;
import com.subscription.subscription_management.AuthResponse;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;

    public AuthService(
            JdbcTemplate jdbcTemplate,
            PasswordEncoder passwordEncoder) {
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    public AuthResponse login(AuthRequest request) {

        if (request.role() == null ||
                request.email() == null ||
                request.password() == null) {
            throw new IllegalArgumentException(
                    "Role, email and password are required");
        }

        String role = request.role().trim().toUpperCase();
        String email = request.email().trim();

        if (role.equals("ADMIN")) {

            var admins = jdbcTemplate.queryForList("""
                    SELECT admin_id, full_name, email,
                           password_hash, status
                    FROM admins
                    WHERE email = ?
                    """, email);

            if (admins.isEmpty()) {
                throw new IllegalArgumentException(
                        "Invalid email or password");
            }

            var admin = admins.get(0);

            String hash = (String) admin.get("password_hash");
            String status = (String) admin.get("status");

            if (!"ACTIVE".equalsIgnoreCase(status) ||
                    hash == null ||
                    !passwordEncoder.matches(
                            request.password(), hash)) {
                throw new IllegalArgumentException(
                        "Invalid email or password");
            }

            return new AuthResponse(
                    "Login successful",
                    "ADMIN",
                    ((Number) admin.get("admin_id")).longValue(),
                    (String) admin.get("full_name")
            );
        }

        if (role.equals("CUSTOMER")) {

            var customers = jdbcTemplate.queryForList("""
                    SELECT customer_id, full_name, email,
                           password_hash, status
                    FROM customers
                    WHERE email = ?
                    """, email);

            if (customers.isEmpty()) {
                throw new IllegalArgumentException(
                        "Invalid email or password");
            }

            var customer = customers.get(0);

            String hash = (String) customer.get("password_hash");
            String status = (String) customer.get("status");

            if (!"ACTIVE".equalsIgnoreCase(status) ||
                    hash == null ||
                    !passwordEncoder.matches(
                            request.password(), hash)) {
                throw new IllegalArgumentException(
                        "Invalid email or password");
            }

            return new AuthResponse(
                    "Login successful",
                    "CUSTOMER",
                    ((Number) customer.get("customer_id")).longValue(),
                    (String) customer.get("full_name")
            );
        }

        throw new IllegalArgumentException("Invalid login role");
    }
}
