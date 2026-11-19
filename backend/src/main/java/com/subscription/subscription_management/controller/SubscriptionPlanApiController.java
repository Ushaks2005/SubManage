
package com.subscription.subscription_management.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/plans")
@CrossOrigin(origins = "*")
public class SubscriptionPlanApiController {

    private final JdbcTemplate jdbcTemplate;

    public SubscriptionPlanApiController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. GET - View all subscription plans
    @GetMapping
    public List<Map<String, Object>> getAllPlans() {

        String sql = """
            SELECT plan_id, plan_name, price,
                   duration_days, usage_limit, status
            FROM subscription_plans
            ORDER BY plan_id
            """;

        return jdbcTemplate.queryForList(sql);
    }

    // 2. GET - View plan by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getPlanById(@PathVariable Long id) {

        List<Map<String, Object>> plans =
                jdbcTemplate.queryForList("""
                    SELECT plan_id, plan_name, price,
                           duration_days, usage_limit, status
                    FROM subscription_plans
                    WHERE plan_id = ?
                    """, id);

        if (plans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(plans.get(0));
    }

    // 3. POST - Create a new subscription plan
    @PostMapping
    public ResponseEntity<?> createPlan(
            @RequestBody PlanRequest request) {

        String error = validatePlan(request);

        if (error != null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", error));
        }

        String sql = """
            INSERT INTO subscription_plans
            (plan_name, price, duration_days, usage_limit, status)
            VALUES (?, ?, ?, ?, 'ACTIVE')
            """;

        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();

            jdbcTemplate.update(connection -> {

                PreparedStatement ps = connection.prepareStatement(
                        sql, Statement.RETURN_GENERATED_KEYS);

                ps.setString(1, request.planName().trim());
                ps.setBigDecimal(2, request.price());
                ps.setInt(3, request.durationDays());
                ps.setInt(4, request.usageLimit());

                return ps;

            }, keyHolder);

            Number generatedId = keyHolder.getKey();

            if (generatedId == null) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of("error", "Could not retrieve plan ID"));
            }

            Long planId = generatedId.longValue();

            Map<String, Object> createdPlan =
                    jdbcTemplate.queryForMap("""
                        SELECT plan_id, plan_name, price,
                               duration_days, usage_limit, status
                        FROM subscription_plans
                        WHERE plan_id = ?
                        """, planId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createdPlan);

        } catch (DuplicateKeyException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error",
                            "A subscription plan with this unique value already exists"));
        }
    }

    // 4. PUT - Update an existing subscription plan
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePlan(
            @PathVariable Long id,
            @RequestBody PlanRequest request) {

        String error = validatePlan(request);

        if (error != null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", error));
        }

        List<Map<String, Object>> existingPlans =
                jdbcTemplate.queryForList(
                        "SELECT plan_id FROM subscription_plans WHERE plan_id = ?",
                        id
                );

        if (existingPlans.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        try {
            jdbcTemplate.update("""
                UPDATE subscription_plans
                SET plan_name = ?,
                    price = ?,
                    duration_days = ?,
                    usage_limit = ?
                WHERE plan_id = ?
                """,
                    request.planName().trim(),
                    request.price(),
                    request.durationDays(),
                    request.usageLimit(),
                    id
            );

            Map<String, Object> updatedPlan =
                    jdbcTemplate.queryForMap("""
                        SELECT plan_id, plan_name, price,
                               duration_days, usage_limit, status
                        FROM subscription_plans
                        WHERE plan_id = ?
                        """, id);

            return ResponseEntity.ok(updatedPlan);

        } catch (DuplicateKeyException e) {

            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error",
                            "A subscription plan with this unique value already exists"));
        }
    }

    // 5. PATCH - Activate or deactivate a plan
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updatePlanStatus(
            @PathVariable Long id,
            @RequestBody PlanStatusRequest request) {

        if (request == null || request.status() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Plan status is required"));
        }

        String status = request.status()
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!status.equals("ACTIVE") &&
                !status.equals("INACTIVE")) {

            return ResponseEntity.badRequest()
                    .body(Map.of("error",
                            "Status must be ACTIVE or INACTIVE"));
        }

        int updated = jdbcTemplate.update("""
            UPDATE subscription_plans
            SET status = ?
            WHERE plan_id = ?
            """, status, id);

        if (updated == 0) {

            List<Map<String, Object>> existing =
                    jdbcTemplate.queryForList(
                            "SELECT plan_id FROM subscription_plans WHERE plan_id = ?",
                            id
                    );

            if (existing.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
        }

        Map<String, Object> updatedPlan =
                jdbcTemplate.queryForMap("""
                    SELECT plan_id, plan_name, price,
                           duration_days, usage_limit, status
                    FROM subscription_plans
                    WHERE plan_id = ?
                    """, id);

        return ResponseEntity.ok(updatedPlan);
    }

    // Validate plan details
    private String validatePlan(PlanRequest request) {

        if (request == null ||
                request.planName() == null ||
                request.price() == null ||
                request.durationDays() == null ||
                request.usageLimit() == null) {

            return "All plan fields are required";
        }

        if (request.planName().isBlank()) {
            return "Plan name cannot be empty";
        }

        if (request.planName().trim().length() > 50) {
            return "Plan name cannot exceed 50 characters";
        }

        if (request.price().compareTo(BigDecimal.ZERO) < 0) {
            return "Plan price cannot be negative";
        }

        if (request.durationDays() <= 0) {
            return "Duration must be greater than zero";
        }

        if (request.usageLimit() < 0) {
            return "Usage limit cannot be negative";
        }

        return null;
    }

    // Request for creating or updating a plan
    public record PlanRequest(
            String planName,
            BigDecimal price,
            Integer durationDays,
            Integer usageLimit
    ) {}

    // Request for activating or deactivating a plan
    public record PlanStatusRequest(
            String status
    ) {}
}
