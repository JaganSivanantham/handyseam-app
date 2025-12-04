package com.handyseam.app.order;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private ShopOrder order;

    @Column(name = "item_id")
    private Long itemId;

    private String styleName;
    private Double price;
    private Integer quantity;
    private String instruction;

    // --- CHANGED: Store image directly in DB ---
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] imageData;

    // Helper to check if image exists (for HTML)
    public boolean hasImage() {
        return imageData != null && imageData.length > 0;
    }
}