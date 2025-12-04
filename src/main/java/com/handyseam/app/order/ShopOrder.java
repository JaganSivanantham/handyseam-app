package com.handyseam.app.order;

import com.handyseam.app.customer.Customer;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
public class ShopOrder {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private LocalDateTime orderDate;
    private LocalDate expectedFittingDate;
    private Double totalAmount = 0.0;
    private String status;
    private String trackingId;
    private boolean isConfirmed;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Payment> payments = new ArrayList<>();

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Measurement measurement;

    // --- NEW HELPER METHODS FOR BALANCE ---

    public Double getPaidAmount() {
        if (payments == null || payments.isEmpty()) return 0.0;
        return payments.stream().mapToDouble(Payment::getAmount).sum();
    }

    public Double getBalanceAmount() {
        return totalAmount - getPaidAmount();
    }
}