package com.subscription.subscription_management.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SecurityTestController {

    @GetMapping("/admin/dashboard")
    public String adminDashboard() {
        return "Welcome to Admin Dashboard!";
    }

    @GetMapping("/customer/dashboard")
    public String customerDashboard() {
        return "Welcome to Customer Dashboard!";
    }
}