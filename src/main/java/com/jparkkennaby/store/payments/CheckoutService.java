package com.jparkkennaby.store.payments;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jparkkennaby.store.auth.AuthService;
import com.jparkkennaby.store.entities.Order;
import com.jparkkennaby.store.exceptions.CartEmptyException;
import com.jparkkennaby.store.exceptions.CartNotFoundException;
import com.jparkkennaby.store.repositories.CartRepository;
import com.jparkkennaby.store.repositories.OrderRepository;
import com.jparkkennaby.store.services.CartService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CheckoutService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;
    private final PaymentGateway paymentGateway;

    /**
     * The checkout method is an Atomic Operation
     * - The service has multiple operations that should all be completed
     * together, or none of them should be completed.
     * - To ensure this behavour, the Transactional annotation is required
     * - The order method is initially saved, but if an exception is thrown, the
     * order should be deleted.
     * 
     * @param request
     * @return
     */

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) {
        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        if (cart.isEmpty()) {
            throw new CartEmptyException();
        }

        var order = Order.fromCart(cart, authService.getCurrentUser());

        // once the order has been saved, under the hood the persistance context gets
        // closed and this order object becomes transient (or tempoarary). However later
        // on we try to delete the order if a stripe exception occurs.
        orderRepository.save(order);

        try {
            var session = paymentGateway.createCheckoutSession(order);
            cartService.clearCart(cart.getId());
            return new CheckoutResponse(order.getId(), session.getCheckoutUrl());
        } catch (PaymentException ex) {
            // without the atomic implemetation (Transactional annotation) of this method,
            // at this moment the order object would be considered transient and unnattached
            // to the persistance context. Adding the transactional annotation keeps the
            // persistance context open for the entire lifecycle of the method
            orderRepository.delete(order);
            throw ex;
        }

    }

    public void handleWebhookEvent(WebhookRequest request) {
        paymentGateway
                .parseWebhookRequest(request)
                .ifPresent(paymentResult -> {
                    var order = orderRepository.findById(Long.valueOf(paymentResult.getOrderId())).orElseThrow();
                    order.setStatus(paymentResult.getPaymentStatus());
                    orderRepository.save(order);
                });
    }

}
