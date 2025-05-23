package com.jparkkennaby.store.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "customer_id")
    @JoinColumn(name = "id")
    @MapsId
    private User customer;

    @Column(name = "status")
    private Status status;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDate createdAt;

    @Column(name = "total_price")
    private BigDecimal price;
}