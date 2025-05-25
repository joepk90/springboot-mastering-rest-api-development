package com.jparkkennaby.store.services;

import com.jparkkennaby.store.entities.Order;

public interface PaymentGateway {

    CheckoutSession createCheckoutSession(Order order);
}
