package com.example.order_management_system.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "shipping")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Shipping {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "shipping_address")
    private String shippingAddress;

    @Column(name = "carrier")
    private String carrier;

    @Column(name = "tracking_number")
    private String trackingNumber;

    @Column(name = "status")
    private String status;

    @Column(name = "shipped_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date shippedAt;

    @Column(name = "delivered_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deliveredAt;

    @OneToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Order order;
}