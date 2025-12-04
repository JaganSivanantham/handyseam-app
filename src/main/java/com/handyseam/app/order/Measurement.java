package com.handyseam.app.order;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Measurement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "order_id")
    private ShopOrder order;

    private String length;
    private String chest;
    private String shoulder;
    private String sleeveLength;
    private String sleeveWidth;
    private String chestPit;
    private String waistPit;
    private String hipPit;
    private String collar;
    private String cuffLength;
    private String cuffWidth;
    private String height;
    private String hip;
    private String seat;
    private String thigh;
    private String knee;
    private String bottom;
    private String inSeam;
}