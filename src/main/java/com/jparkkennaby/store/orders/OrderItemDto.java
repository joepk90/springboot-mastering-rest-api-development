package com.jparkkennaby.store.orders;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemDto {
    private ProductDto product;
    private int quantity;
    private BigDecimal totalPrice;
}
