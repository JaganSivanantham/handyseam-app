package com.handyseam.app.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MeasurementRepository extends JpaRepository<Measurement, Long> {

    // Find all measurements for a customer, ordered by newest first
    @Query("SELECT m FROM Measurement m JOIN m.order o WHERE o.customer.id = :customerId ORDER BY o.id DESC")
    List<Measurement> findLatestByCustomer(@Param("customerId") Long customerId);
}