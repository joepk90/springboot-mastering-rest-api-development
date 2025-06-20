package com.jparkkennaby.store.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.UUID;

import org.hibernate.type.SqlTypes;

import com.jparkkennaby.store.products.Product;

import org.hibernate.annotations.JdbcTypeCode;

@Getter
@Setter
@Entity
@Table(name = "carts")
public class Cart {

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR) // tells spring/hybernate to store as VARCHAR, not binary
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "date_created", updatable = false)
    private LocalDate dateCreated;

    @PrePersist
    public void prePersist() {
        if (dateCreated == null) {
            dateCreated = LocalDate.now();
        }
    }

    // orphanRemoval (true): deletes the cart item record if becomes orhpaned
    @OneToMany(mappedBy = "cart", cascade = CascadeType.MERGE, orphanRemoval = true, fetch = FetchType.EAGER)
    private Set<CartItem> items = new LinkedHashSet<>();

    public BigDecimal getTotalPrice() {
        return items.stream()
                .map(CartItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add); // add each iteration together
    }

    public CartItem getItem(Long productId) {
        return items.stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst().orElse(null);
    }

    public CartItem addItem(Product product) {
        var cartItem = getItem(product.getId());

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(this);
            cartItem.setQuantity(1);

            items.add(cartItem);
        }
        return cartItem;
    }

    public void removeItem(Long productId) {
        var cartItem = getItem(productId);

        if (cartItem != null) {
            getItems().remove(cartItem);
            cartItem.setCart(null);
        }
    }

    public void clear() {
        items.clear();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

}
