
package com.subscription.subscription_management.controller;

import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/subscriptions")
@CrossOrigin(origins = "*")
public class SubscriptionApiController {

    private final JdbcTemplate jdbcTemplate;

    public SubscriptionApiController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 1. GET - View all subscriptions
    @GetMapping
    public List<Map<String, Object>> getAllSubscriptions() {

        String sql = """
            SELECT s.subscription_id,
                   s.customer_id,
                   c.full_name AS customer_name,
                   s.plan_id,
                   p.plan_name,
                   s.agreed_price,
                   s.start_date,
                   s.end_date,
                   s.status
            FROM subscriptions s
            JOIN customers c ON s.customer_id = c.customer_id
            JOIN subscription_plans p ON s.plan_id = p.plan_id
            ORDER BY s.subscription_id
            """;

        return jdbcTemplate.queryForList(sql);
    }

    // 2. POST - Create a subscription
    @PostMapping
    public ResponseEntity<?> createSubscription(
            @RequestBody SubscriptionRequest request) {

        if (request.customerId() == null ||
                request.planId() == null ||
                request.startDate() == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("error",
                            "Customer ID, Plan ID and Start Date are required")
            );
        }

        // Check customer
        Integer customerCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM customers WHERE customer_id = ?",
                Integer.class,
                request.customerId()
        );

        if (customerCount == null || customerCount == 0) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Customer does not exist")
            );
        }

        // Get active plan details
        List<Map<String, Object>> plans = jdbcTemplate.queryForList(
                """
                SELECT price, duration_days
                FROM subscription_plans
                WHERE plan_id = ? AND status = 'ACTIVE'
                """,
                request.planId()
        );

        if (plans.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Active subscription plan not found")
            );
        }

        BigDecimal price =
                (BigDecimal) plans.get(0).get("price");

        Integer duration =
                ((Number) plans.get(0).get("duration_days")).intValue();

        LocalDate startDate = request.startDate();
        LocalDate endDate = startDate.plusDays(duration);

        String sql = """
            INSERT INTO subscriptions
            (customer_id, plan_id, agreed_price,
             start_date, end_date, status)
            VALUES (?, ?, ?, ?, ?, 'PENDING_PAYMENT')
            """;

        try {
            jdbcTemplate.update(
                    sql,
                    request.customerId(),
                    request.planId(),
                    price,
                    startDate,
                    endDate
            );

            Long subscriptionId = jdbcTemplate.queryForObject(
                    "SELECT LAST_INSERT_ID()",
                    Long.class
            );

            Map<String, Object> createdSubscription =
                    jdbcTemplate.queryForMap("""
                    SELECT subscription_id, customer_id, plan_id,
                           agreed_price, start_date, end_date, status
                    FROM subscriptions
                    WHERE subscription_id = ?
                    """, subscriptionId);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(createdSubscription);

        } catch (DuplicateKeyException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error",
                            "Subscription could not be created due to a duplicate record"));
        }
    }

    // 3. GET - View subscription by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getSubscriptionById(
            @PathVariable Long id) {

        List<Map<String, Object>> subscriptions =
                jdbcTemplate.queryForList("""
                SELECT s.subscription_id,
                       s.customer_id,
                       c.full_name AS customer_name,
                       s.plan_id,
                       p.plan_name,
                       s.agreed_price,
                       s.start_date,
                       s.end_date,
                       s.status
                FROM subscriptions s
                JOIN customers c ON s.customer_id = c.customer_id
                JOIN subscription_plans p ON s.plan_id = p.plan_id
                WHERE s.subscription_id = ?
                """, id);

        if (subscriptions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(subscriptions.get(0));
    }

    // 4. GET - View subscriptions by customer ID
    @GetMapping("/customer/{customerId}")
    public List<Map<String, Object>> getSubscriptionsByCustomer(
            @PathVariable Long customerId) {

        return jdbcTemplate.queryForList("""
                SELECT subscription_id, customer_id, plan_id,
                       agreed_price, start_date, end_date, status
                FROM subscriptions
                WHERE customer_id = ?
                ORDER BY subscription_id
                """, customerId);
    }

    // 5. PUT - Update subscription
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSubscription(
            @PathVariable Long id,
            @RequestBody UpdateSubscriptionRequest request) {

        if (request.planId() == null ||
                request.startDate() == null) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Plan ID and Start Date are required")
            );
        }

        List<Map<String, Object>> plans = jdbcTemplate.queryForList(
                """
                SELECT price, duration_days
                FROM subscription_plans
                WHERE plan_id = ? AND status = 'ACTIVE'
                """,
                request.planId()
        );

        if (plans.isEmpty()) {
            return ResponseEntity.badRequest().body(
                    Map.of("error", "Active subscription plan not found")
            );
        }

        BigDecimal price =
                (BigDecimal) plans.get(0).get("price");

        Integer duration =
                ((Number) plans.get(0).get("duration_days")).intValue();

        LocalDate endDate = request.startDate().plusDays(duration);

        int updated = jdbcTemplate.update("""
                UPDATE subscriptions
                SET plan_id = ?,
                    agreed_price = ?,
                    start_date = ?,
                    end_date = ?
                WHERE subscription_id = ?
                """,
                request.planId(),
                price,
                request.startDate(),
                endDate,
                id
        );

        if (updated == 0) {
            return ResponseEntity.notFound().build();
        }

        Map<String, Object> result = jdbcTemplate.queryForMap("""
                SELECT subscription_id, customer_id, plan_id,
                       agreed_price, start_date, end_date, status
                FROM subscriptions
                WHERE subscription_id = ?
                """, id);

        return ResponseEntity.ok(result);
    }

    // 6. PATCH - Cancel subscription
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<?> cancelSubscription(
            @PathVariable Long id) {

        int updated = jdbcTemplate.update("""
                UPDATE subscriptions
                SET status = 'CANCELLED'
                WHERE subscription_id = ?
                  AND status <> 'CANCELLED'
                """, id);

        if (updated == 0) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(
                Map.of(
                        "message", "Subscription cancelled successfully",
                        "subscriptionId", id
                )
        );
    }

    // Request for creating a subscription
    public record SubscriptionRequest(
            Long customerId,
            Long planId,
            LocalDate startDate
    ) {}

    // Request for updating a subscription
    public record UpdateSubscriptionRequest(
            Long planId,
            LocalDate startDate
    ) {}
}
