
package com.subscription.subscription_management.service;

import com.subscription.subscription_management.model.Subscription;
import com.subscription.subscription_management.repository.SubscriptionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    // Get all subscriptions
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    // Get one subscription by ID
    public Subscription getSubscriptionById(int subscriptionId) {
        return subscriptionRepository.findById(subscriptionId);
    }
}
