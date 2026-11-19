
package com.subscription.subscription_management.repository;

import com.subscription.subscription_management.model.UsageRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UsageRecordRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsageRecordRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<UsageRecord> usageRowMapper = (rs, rowNum) -> {
        UsageRecord usage = new UsageRecord();

        usage.setUsageId(rs.getInt("usage_id"));
        usage.setSubscriptionId(rs.getInt("subscription_id"));
        usage.setUnitsUsed(rs.getInt("units_used"));
        usage.setUsageDate(rs.getDate("usage_date").toLocalDate());
        usage.setDescription(rs.getString("description"));

        return usage;
    };

    // Get all usage records
    public List<UsageRecord> findAll() {
        String sql = "SELECT usage_id, subscription_id, units_used, " +
                "usage_date, description FROM usage_records";

        return jdbcTemplate.query(sql, usageRowMapper);
    }

    // Get usage record by ID
    public UsageRecord findById(int usageId) {
        String sql = "SELECT usage_id, subscription_id, units_used, " +
                "usage_date, description FROM usage_records WHERE usage_id = ?";

        return jdbcTemplate.queryForObject(
                sql,
                usageRowMapper,
                usageId
        );
    }
}
