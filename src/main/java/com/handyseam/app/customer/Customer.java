package com.handyseam.app.customer;

import jakarta.persistence.*;
import lombok.Data; // Remove if not using Lombok and generate Getters/Setters manually
import java.time.LocalDateTime;

@Entity
@Data // Lombok annotation for Getters, Setters, ToString, etc.
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private String email;
    private String address;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] photo;

    // Helper for Thymeleaf
    public boolean hasPhoto() {
        return photo != null && photo.length > 0;
    }
}