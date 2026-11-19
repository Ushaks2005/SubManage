
package com.subscription.subscription_management;

import com.subscription.subscription_management.model.Customer;
import com.subscription.subscription_management.service.CustomerService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CustomerController {

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping("/customers")
    public String showCustomers(Model model) {

        List<Customer> customers =
                customerService.getAllCustomers();

        model.addAttribute("customers", customers);

        return "customers";
    }
}
