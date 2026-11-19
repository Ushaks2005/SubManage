
package com.subscription.subscription_management.service;

import com.subscription.subscription_management.model.SubscriptionPlan;
import com.subscription.subscription_management.repository.SubscriptionPlanRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionPlanService {

    private final SubscriptionPlanRepository subscriptionPlanRepository;

    public SubscriptionPlanService(SubscriptionPlanRepository subscriptionPlanRepository) {
        this.subscriptionPlanRepository = subscriptionPlanRepository;
    }

    // Get all subscription plans
    public List<SubscriptionPlan> getAllPlans() {
        return subscriptionPlanRepository.findAll();
    }

    // Get subscription plan by ID
    public SubscriptionPlan getPlanById(int planId) {
        return subscriptionPlanRepository.findById(planId);
    }
}
