
package com.subscription.subscription_management;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class UsageController {

    private final JdbcTemplate jdbcTemplate;

    public UsageController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/usage-records")
    public String showUsageRecords(Model model) {

        List<Map<String, Object>> usageRecords =
            jdbcTemplate.queryForList(
                "SELECT " +
                "u.usage_id, " +
                "c.full_name, " +
                "u.subscription_id, " +
                "u.usage_date, " +
                "u.units_used, " +
                "u.description " +
                "FROM usage_records u " +
                "JOIN subscriptions s " +
                "ON u.subscription_id = s.subscription_id " +
                "JOIN customers c " +
                "ON s.customer_id = c.customer_id " +
                "ORDER BY u.usage_id"
            );

        model.addAttribute("usageRecords", usageRecords);

        return "usage_records";
    }
}
