package com.jparkkennaby.store.exceptions;

public class CartEmptyException extends RuntimeException {
    public CartEmptyException() {
        super("Cart is empty"); // default message
    }
}
