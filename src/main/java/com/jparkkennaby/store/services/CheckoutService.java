package com.jparkkennaby.store.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jparkkennaby.store.dtos.CheckoutRequest;
import com.jparkkennaby.store.dtos.CheckoutResponse;
import com.jparkkennaby.store.entities.Order;
import com.jparkkennaby.store.exceptions.CartEmptyException;
import com.jparkkennaby.store.exceptions.CartNotFoundException;
import com.jparkkennaby.store.repositories.CartRepository;
import com.jparkkennaby.store.repositories.OrderRepository;
import com.stripe.param.checkout.SessionCreateParams;

import lombok.RequiredArgsConstructor;

/**
 * RequiredArgsConstructor annotation is used to handle the websiteUrl property
 * - As there is no bean of type String, spring doesn't know what to use to
 * initialise the websiteUrl property with
 * - using RequiredArgsConstructor means spring will only initialise the fields
 * declared as final
 * - The Value annotation will then kick in and provide the configuration
 * setting
 */

@RequiredArgsConstructor
@Service
public class CheckoutService {
    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final AuthService authService;
    private final CartService cartService;

    @Value("${websiteUrl}")
    private String websiteUrl;

    public CheckoutResponse checkout(CheckoutRequest request) {
        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        if (cart.isEmpty()) {
            throw new CartEmptyException();
        }

        var order = Order.fromCart(cart, authService.getCurrentUser());

        orderRepository.save(order);

        // create checkout session
        SessionCreateParams.builder()
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getId())
                .setCancelUrl(websiteUrl + "/checkout-cancel");

        cartService.clearCart(cart.getId());

        return new CheckoutResponse(order.getId());
    }
}
