
package com.subscription.subscription_management;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class PaymentController {

    private final JdbcTemplate jdbcTemplate;

    public PaymentController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/payments")
    public String showPayments(Model model) {

        List<Map<String, Object>> payments =
            jdbcTemplate.queryForList(
                "SELECT " +
                "p.payment_id, " +
                "c.full_name, " +
                "p.subscription_id, " +
                "p.amount, " +
                "p.payment_date, " +
                "p.payment_method, " +
                "p.payment_status " +
                "FROM payments p " +
                "JOIN subscriptions s " +
                "ON p.subscription_id = s.subscription_id " +
                "JOIN customers c " +
                "ON s.customer_id = c.customer_id " +
                "ORDER BY p.payment_id"
            );

        model.addAttribute("payments", payments);

        return "payments";
    }
}
