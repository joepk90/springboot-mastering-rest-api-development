package com.jparkkennaby.store.exceptions;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException() {
        super("Order not found"); // default message
    }
}
