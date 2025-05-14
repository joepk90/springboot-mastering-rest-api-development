package com.jparkkennaby.store.entities;

import jakarta.persistence.*;

import java.util.UUID;

@Entity
@Table(name = "carts")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @Column(name = "product_id")
    private Long productId;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @Column(name = "cart_id")
    private UUID cartId;

    @Column(name = "quantity")
    private Integer quantity;
}
