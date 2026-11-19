
package com.subscription.subscription_management.service;

import com.subscription.subscription_management.model.Customer;
import com.subscription.subscription_management.repository.CustomerRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    // Get all customers
    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // Get one customer by ID
    public Customer getCustomerById(int customerId) {
        return customerRepository.findById(customerId);
    }
}
