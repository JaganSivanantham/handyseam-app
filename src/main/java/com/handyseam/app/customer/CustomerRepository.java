package com.handyseam.app.customer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    // This allows searching by Name OR Phone number
    List<Customer> findByNameContainingOrPhoneContaining(String name, String phone);
}