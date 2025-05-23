package com.jparkkennaby.store.dtos;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderItemDto {
    private OrderProductDto product;
    private int quantity;
    private BigDecimal totalPrice;
}
