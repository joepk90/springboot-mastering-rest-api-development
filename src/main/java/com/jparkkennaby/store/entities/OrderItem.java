package com.jparkkennaby.store.entities;

import java.math.BigDecimal;

import jakarta.persistence.*;

@Table(name = "order_items")
public class OrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "order_id")
    @JoinColumn(name = "id")
    @MapsId
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "product_id")
    @JoinColumn(name = "id")
    @MapsId
    private Product product;

    @Column(name = "unit_price")
    private BigDecimal unit_price;

    @Column(name = "quantity")
    private Integer quantity;

    @Column(name = "total_price")
    private BigDecimal price;
}