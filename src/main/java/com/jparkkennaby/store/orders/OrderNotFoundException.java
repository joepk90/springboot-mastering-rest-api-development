package com.jparkkennaby.store.orders;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("Order not found"); // default message
    }
}
