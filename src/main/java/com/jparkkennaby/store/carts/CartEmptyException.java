package com.jparkkennaby.store.carts;

public class CartEmptyException extends RuntimeException {
    public CartEmptyException() {
        super("Cart is empty"); // default message
    }
}
