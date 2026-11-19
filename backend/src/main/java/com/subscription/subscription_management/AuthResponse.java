package com.subscription.subscription_management;

public record AuthResponse(
        String message,
        String role,
        Long userId,
        String fullName
) {
}