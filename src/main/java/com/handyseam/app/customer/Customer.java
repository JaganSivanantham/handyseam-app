package com.handyseam.app.customer;

import jakarta.persistence.*;
import lombok.Data; 
import java.time.LocalDateTime;

@Entity
@Data 
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

   
    public boolean hasPhoto() {
        return photo != null && photo.length > 0;
    }
}
