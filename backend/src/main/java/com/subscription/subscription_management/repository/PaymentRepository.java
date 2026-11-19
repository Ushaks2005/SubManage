
package com.subscription.subscription_management.repository;

import com.subscription.subscription_management.model.Payment;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Date;
import java.util.List;

@Repository
public class PaymentRepository {

    private final JdbcTemplate jdbcTemplate;

    public PaymentRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Payment> paymentRowMapper = (rs, rowNum) -> {
        Payment payment = new Payment();

        payment.setPaymentId(rs.getInt("payment_id"));
        payment.setSubscriptionId(rs.getInt("subscription_id"));
        payment.setAmount(rs.getBigDecimal("amount"));
        payment.setPaymentDate(
                rs.getDate("payment_date").toLocalDate()
        );
        payment.setPaymentMethod(rs.getString("payment_method"));
        payment.setPaymentStatus(rs.getString("payment_status"));

        return payment;
    };

    // Get all payments
    public List<Payment> findAll() {
        String sql = "SELECT payment_id, subscription_id, amount, " +
                "payment_date, payment_method, payment_status " +
                "FROM payments ORDER BY payment_id";

        return jdbcTemplate.query(sql, paymentRowMapper);
    }

    // Get payment by ID
    public Payment findById(int paymentId) {
        String sql = "SELECT payment_id, subscription_id, amount, " +
                "payment_date, payment_method, payment_status " +
                "FROM payments WHERE payment_id = ?";

        try {
            return jdbcTemplate.queryForObject(
                    sql, paymentRowMapper, paymentId
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    // Check whether subscription exists
    public boolean subscriptionExists(int subscriptionId) {
        String sql = "SELECT COUNT(*) FROM subscriptions " +
                "WHERE subscription_id = ?";

        Integer count = jdbcTemplate.queryForObject(
                sql, Integer.class, subscriptionId
        );

        return count != null && count > 0;
    }

    // Add payment
    public Payment save(Payment payment) {
        String sql = "INSERT INTO payments " +
                "(subscription_id, amount, payment_date, " +
                "payment_method, payment_status) " +
                "VALUES (?, ?, ?, ?, ?)";

        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(
                    sql, Statement.RETURN_GENERATED_KEYS
            );

            ps.setInt(1, payment.getSubscriptionId());
            ps.setBigDecimal(2, payment.getAmount());
            ps.setDate(3, Date.valueOf(payment.getPaymentDate()));
            ps.setString(4, payment.getPaymentMethod());
            ps.setString(5, payment.getPaymentStatus());

            return ps;
        }, keyHolder);

        if (keyHolder.getKey() != null) {
            payment.setPaymentId(keyHolder.getKey().intValue());
        }

        return payment;
    }

    // Update payment details
    public boolean update(int paymentId, Payment payment) {
        String sql = "UPDATE payments SET " +
                "subscription_id = ?, amount = ?, payment_date = ?, " +
                "payment_method = ?, payment_status = ? " +
                "WHERE payment_id = ?";

        int rows = jdbcTemplate.update(
                sql,
                payment.getSubscriptionId(),
                payment.getAmount(),
                Date.valueOf(payment.getPaymentDate()),
                payment.getPaymentMethod(),
                payment.getPaymentStatus(),
                paymentId
        );

        return rows > 0;
    }

    // Update payment status only
    public boolean updateStatus(int paymentId, String status) {
        String sql = "UPDATE payments SET payment_status = ? " +
                "WHERE payment_id = ?";

        int rows = jdbcTemplate.update(sql, status, paymentId);

        return rows > 0;
    }

    // Delete payment
    public boolean delete(int paymentId) {
        String sql = "DELETE FROM payments WHERE payment_id = ?";

        int rows = jdbcTemplate.update(sql, paymentId);

        return rows > 0;
    }
}
