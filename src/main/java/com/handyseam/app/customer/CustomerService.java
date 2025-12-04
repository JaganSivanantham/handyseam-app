package com.handyseam.app.customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    // New method for search
    public List<Customer> searchCustomers(String keyword) {
        if (keyword != null && !keyword.isEmpty()) {
            return customerRepository.findByNameContainingOrPhoneContaining(keyword, keyword);
        }
        return customerRepository.findAll();
    }

    public Customer saveCustomer(Customer customer) {
        return customerRepository.save(customer);
    }
}