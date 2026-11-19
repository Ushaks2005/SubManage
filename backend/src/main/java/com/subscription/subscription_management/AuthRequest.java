package com.subscription.subscription_management;

public record AuthRequest(
        String role,
        String email,
        String password
) {
}