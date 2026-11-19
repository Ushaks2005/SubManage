
package com.subscription.subscription_management.repository;

import com.subscription.subscription_management.model.Subscription;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class SubscriptionRepository {

    private final JdbcTemplate jdbcTemplate;

    public SubscriptionRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Subscription> subscriptionRowMapper = (rs, rowNum) -> {
        Subscription subscription = new Subscription();

        subscription.setSubscriptionId(rs.getInt("subscription_id"));
        subscription.setCustomerId(rs.getInt("customer_id"));
        subscription.setPlanId(rs.getInt("plan_id"));
        subscription.setAgreedPrice(rs.getBigDecimal("agreed_price"));
        subscription.setStartDate(rs.getDate("start_date").toLocalDate());
        subscription.setEndDate(rs.getDate("end_date").toLocalDate());
        subscription.setStatus(rs.getString("status"));

        return subscription;
    };

    // Get all subscriptions
    public List<Subscription> findAll() {
        String sql = "SELECT subscription_id, customer_id, plan_id, " +
                "agreed_price, start_date, end_date, status FROM subscriptions";

        return jdbcTemplate.query(sql, subscriptionRowMapper);
    }

    // Get subscription by ID
    public Subscription findById(int subscriptionId) {
        String sql = "SELECT subscription_id, customer_id, plan_id, " +
                "agreed_price, start_date, end_date, status " +
                "FROM subscriptions WHERE subscription_id = ?";

        return jdbcTemplate.queryForObject(
                sql,
                subscriptionRowMapper,
                subscriptionId
        );
    }
}
