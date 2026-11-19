
package com.subscription.subscription_management.service;

import com.subscription.subscription_management.model.UsageRecord;
import com.subscription.subscription_management.repository.UsageRecordRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsageRecordService {

    private final UsageRecordRepository usageRecordRepository;

    public UsageRecordService(UsageRecordRepository usageRecordRepository) {
        this.usageRecordRepository = usageRecordRepository;
    }

    // Get all usage records
    public List<UsageRecord> getAllUsageRecords() {
        return usageRecordRepository.findAll();
    }

    // Get usage record by ID
    public UsageRecord getUsageRecordById(int usageId) {
        return usageRecordRepository.findById(usageId);
    }
}

