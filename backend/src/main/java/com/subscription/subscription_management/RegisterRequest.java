
package com.subscription.subscription_management;

public record RegisterRequest(
        String fullName,
        String email,
        String phone,
        String password
) {}
