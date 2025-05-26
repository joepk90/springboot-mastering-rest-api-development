package com.jparkkennaby.store.services;

import java.util.Optional;

import com.jparkkennaby.store.entities.Order;

public interface PaymentGateway {

    CheckoutSession createCheckoutSession(Order order);

    Optional<PaymentResult> parseWebhookRequest(WebhookRequest request);
}
