package com.jparkkennaby.store.carts;

public class CartNotFoundException extends RuntimeException {
    public CartNotFoundException() {
        super("Cart not found"); // default message
    }
}
