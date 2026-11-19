
package com.subscription.subscription_management;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final JdbcTemplate jdbcTemplate;

    public AdminController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @GetMapping("/admin")
    public String showAdminDashboard(Model model) {

        int totalCustomers = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM customers", Integer.class);

        int activeSubscriptions = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM subscriptions WHERE status = 'ACTIVE'",
            Integer.class);

        double totalRevenue = jdbcTemplate.queryForObject(
            "SELECT COALESCE(SUM(amount), 0) FROM payments",
            Double.class);

        int totalUsageRecords = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM usage_records",
            Integer.class);

        int basicCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM subscriptions s " +
            "JOIN subscription_plans sp ON s.plan_id = sp.plan_id " +
            "WHERE sp.plan_name = 'Basic'",
            Integer.class);

        int standardCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM subscriptions s " +
            "JOIN subscription_plans sp ON s.plan_id = sp.plan_id " +
            "WHERE sp.plan_name = 'Standard'",
            Integer.class);

        int premiumCount = jdbcTemplate.queryForObject(
            "SELECT COUNT(*) FROM subscriptions s " +
            "JOIN subscription_plans sp ON s.plan_id = sp.plan_id " +
            "WHERE sp.plan_name = 'Premium'",
            Integer.class);

        model.addAttribute("totalCustomers", totalCustomers);
        model.addAttribute("activeSubscriptions", activeSubscriptions);
        model.addAttribute("totalRevenue", totalRevenue);
        model.addAttribute("totalUsageRecords", totalUsageRecords);

        model.addAttribute("basicCount", basicCount);
        model.addAttribute("standardCount", standardCount);
        model.addAttribute("premiumCount", premiumCount);

        return "admin";
    }
}
