
package com.subscription.subscription_management;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class SubscriptionController {

    private final JdbcTemplate jdbcTemplate;

    public SubscriptionController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/subscriptions")
    public String showSubscriptions(Model model) {

        List<Map<String, Object>> subscriptions =
            jdbcTemplate.queryForList(
                "SELECT " +
                "s.subscription_id, " +
                "c.full_name, " +
                "sp.plan_name, " +
                "s.agreed_price, " +
                "s.start_date, " +
                "s.end_date, " +
                "s.status " +
                "FROM subscriptions s " +
                "JOIN customers c ON s.customer_id = c.customer_id " +
                "JOIN subscription_plans sp ON s.plan_id = sp.plan_id " +
                "ORDER BY s.subscription_id"
            );

        model.addAttribute("subscriptions", subscriptions);

        return "subscriptions";
    }
}

