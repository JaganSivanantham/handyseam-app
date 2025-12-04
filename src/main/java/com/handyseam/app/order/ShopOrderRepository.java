package com.handyseam.app.order;

import com.handyseam.app.customer.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ShopOrderRepository extends JpaRepository<ShopOrder, Long> {

    // Existing methods...
    List<ShopOrder> findTop5ByOrderByOrderDateDesc();

    @Query("SELECT COUNT(o) FROM ShopOrder o WHERE o.status <> 'Delivered'")
    Long countOrdersPending();

    @Query("SELECT SUM(o.totalAmount) FROM ShopOrder o")
    Double sumTotalRevenue();

    // --- NEW METHOD ---
    List<ShopOrder> findByCustomer(Customer customer);
}