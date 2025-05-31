package com.jparkkennaby.store.exceptions;

public class MaxTableRecordLimitReached extends RuntimeException {
    public MaxTableRecordLimitReached(String message) {
        super(message);
    }
}
