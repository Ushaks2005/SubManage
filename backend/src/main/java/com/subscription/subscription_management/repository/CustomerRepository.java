
package com.subscription.subscription_management.repository;

import com.subscription.subscription_management.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class CustomerRepository {

    private final JdbcTemplate jdbcTemplate;

    public CustomerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Customer> customerRowMapper = (rs, rowNum) -> {
        Customer customer = new Customer();

        customer.setCustomerId(rs.getInt("customer_id"));
        customer.setFullName(rs.getString("full_name"));
        customer.setEmail(rs.getString("email"));
        customer.setPhone(rs.getString("phone"));
        customer.setPasswordHash(rs.getString("password_hash"));
        customer.setStatus(rs.getString("status"));

        return customer;
    };

    public List<Customer> findAll() {
        String sql = "SELECT customer_id, full_name, email, phone, password_hash, status FROM customers";

        return jdbcTemplate.query(sql, customerRowMapper);
    }

    public Customer findById(int customerId) {
        String sql = "SELECT customer_id, full_name, email, phone, password_hash, status FROM customers WHERE customer_id = ?";

        return jdbcTemplate.queryForObject(
                sql,
                customerRowMapper,
                customerId
        );
    }
}
