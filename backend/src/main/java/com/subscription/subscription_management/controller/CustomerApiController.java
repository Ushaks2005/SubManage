
package com.subscription.subscription_management.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerApiController {

    private final JdbcTemplate jdbcTemplate;

    public CustomerApiController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. GET - Retrieve all customers
    @GetMapping
    public List<Map<String, Object>> getAllCustomers() {

        String sql = """
            SELECT customer_id, full_name, email,
                   phone, status, created_at
            FROM customers
            ORDER BY customer_id
            """;

        return jdbcTemplate.queryForList(sql);
    }

    // 2. POST - Add a new customer
    @PostMapping
    public ResponseEntity<?> addCustomer(
            @RequestBody CustomerRequest customer) {

        if (!isValid(customer)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            "Name, email and phone are required"));
        }

        String sql = """
            INSERT INTO customers
            (full_name, email, phone, status)
            VALUES (?, ?, ?, 'ACTIVE')
            """;

        KeyHolder keyHolder = new GeneratedKeyHolder();

        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS);

                ps.setString(1, customer.fullName().trim());
                ps.setString(2, customer.email().trim());
                ps.setString(3, customer.phone().trim());

                return ps;
            }, keyHolder);

            Number generatedId = keyHolder.getKey();

            if (generatedId == null) {
                return ResponseEntity.status(
                                HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error",
                                "Could not retrieve customer ID"));
            }

            Map<String, Object> savedCustomer =
                    jdbcTemplate.queryForMap("""
                    SELECT customer_id, full_name, email,
                           phone, status, created_at
                    FROM customers
                    WHERE customer_id = ?
                    """, generatedId.longValue());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedCustomer);

        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error",
                            "Customer email or phone already exists"));
        }
    }

    // 3. GET - Retrieve customer by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getCustomerById(
            @PathVariable Long id) {

        String sql = """
            SELECT customer_id, full_name, email,
                   phone, status, created_at
            FROM customers
            WHERE customer_id = ?
            """;

        List<Map<String, Object>> customers =
                jdbcTemplate.queryForList(sql, id);

        if (customers.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(customers.get(0));
    }

    // 4. PUT - Update customer by ID
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCustomer(
            @PathVariable Long id,
            @RequestBody CustomerRequest customer) {

        if (!isValid(customer)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            "Name, email and phone are required"));
        }

        String sql = """
            UPDATE customers
            SET full_name = ?, email = ?, phone = ?
            WHERE customer_id = ?
            """;

        try {
            int rowsUpdated = jdbcTemplate.update(
                    sql,
                    customer.fullName().trim(),
                    customer.email().trim(),
                    customer.phone().trim(),
                    id
            );

            if (rowsUpdated == 0) {
                return ResponseEntity.notFound().build();
            }

            Map<String, Object> updatedCustomer =
                    jdbcTemplate.queryForMap("""
                    SELECT customer_id, full_name, email,
                           phone, status, created_at
                    FROM customers
                    WHERE customer_id = ?
                    """, id);

            return ResponseEntity.ok(updatedCustomer);

        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error",
                            "Customer email or phone already exists"));
        }
    }

    // Common request validation
    private boolean isValid(CustomerRequest customer) {
        return customer != null
                && customer.fullName() != null
                && !customer.fullName().isBlank()
                && customer.email() != null
                && !customer.email().isBlank()
                && customer.phone() != null
                && !customer.phone().isBlank();
    }

    // Request body structure
    public record CustomerRequest(
            String fullName,
            String email,
            String phone
    ) {
    }
}
