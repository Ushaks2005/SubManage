
package com.subscription.subscription_management.repository;

import com.subscription.subscription_management.model.SubscriptionPlan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubscriptionPlanRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubscriptionPlanRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<SubscriptionPlan> planRowMapper = (rs, rowNum) -> {
        SubscriptionPlan plan = new SubscriptionPlan();

        plan.setPlanId(rs.getInt("plan_id"));
        plan.setPlanName(rs.getString("plan_name"));
        plan.setPrice(rs.getBigDecimal("price"));
        plan.setUsageLimit(rs.getInt("usage_limit"));
        plan.setStatus(rs.getString("status"));
        plan.setDurationDays(rs.getInt("duration_days"));

        return plan;
    };

    // Get all subscription plans
    public List<SubscriptionPlan> findAll() {
        String sql = "SELECT plan_id, plan_name, price, usage_limit, " +
                "status, duration_days FROM plans";

        return jdbcTemplate.query(sql, planRowMapper);
    }

    // Get subscription plan by ID
    public SubscriptionPlan findById(int planId) {
        String sql = "SELECT plan_id, plan_name, price, usage_limit, " +
                "status, duration_days FROM plans WHERE plan_id = ?";

        return jdbcTemplate.queryForObject(
                sql,
                planRowMapper,
                planId
        );
    }
}
