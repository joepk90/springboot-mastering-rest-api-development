package com.jparkkennaby.store.services;

import com.jparkkennaby.store.entities.PaymentStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class PaymentResult {
    private Long orderId;
    private PaymentStatus paymentStatus;
}
