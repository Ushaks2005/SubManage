
package com.subscription.subscription_management.repository;

import com.subscription.subscription_management.model.Invoice;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InvoiceRepository {

    private final JdbcTemplate jdbcTemplate;

    public InvoiceRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Invoice> invoiceRowMapper = (rs, rowNum) -> {
        Invoice invoice = new Invoice();

        invoice.setInvoiceId(rs.getInt("invoice_id"));
        invoice.setSubscriptionId(rs.getInt("subscription_id"));
        invoice.setCustomerId(rs.getInt("customer_id"));
        invoice.setInvoiceNumber(rs.getString("invoice_number"));
        invoice.setInvoiceDate(
                rs.getDate("invoice_date").toLocalDate()
        );
        invoice.setDueDate(
                rs.getDate("due_date").toLocalDate()
        );
        invoice.setAmount(rs.getBigDecimal("amount"));
        invoice.setInvoiceStatus(rs.getString("invoice_status"));

        return invoice;
    };

    // Get all invoices
    public List<Invoice> findAll() {
        String sql = "SELECT * FROM invoices ORDER BY invoice_id";

        return jdbcTemplate.query(sql, invoiceRowMapper);
    }

    // Get invoice by ID
    public Invoice findById(int invoiceId) {
        String sql = "SELECT * FROM invoices WHERE invoice_id = ?";

        try {
            return jdbcTemplate.queryForObject(
                    sql,
                    invoiceRowMapper,
                    invoiceId
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
