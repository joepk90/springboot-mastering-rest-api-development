package com.jparkkennaby.store.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.jparkkennaby.store.dtos.CheckoutRequest;
import com.jparkkennaby.store.dtos.CheckoutResponse;
import com.jparkkennaby.store.entities.Order;
import com.jparkkennaby.store.exceptions.CartEmptyException;
import com.jparkkennaby.store.exceptions.CartNotFoundException;
import com.jparkkennaby.store.repositories.CartRepository;
import com.jparkkennaby.store.repositories.OrderRepository;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
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
     * @throws StripeException
     */

    @Transactional
    public CheckoutResponse checkout(CheckoutRequest request) throws StripeException {
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
            // create checkout session
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel");

            order.getItems().forEach(item -> {
                var lineItem = SessionCreateParams.LineItem.builder()
                        .setQuantity(Long.valueOf(item.getQuantity()))
                        .setPriceData(
                                SessionCreateParams.LineItem.PriceData.builder()
                                        .setCurrency("usd")
                                        .setUnitAmountDecimal(item.getUnitPrice())
                                        .setProductData(
                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                        .setName(item.getProduct().getName()).build())
                                        .build())
                        .build();
                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());

            cartService.clearCart(cart.getId());

            return new CheckoutResponse(order.getId(), session.getUrl());
        } catch (StripeException ex) {
            // without the atomic implemetation (Transactional annotation) of this method,
            // at this moment the order object would be considered transient and unnattached
            // to the persistance context. Adding the transactional annotation keeps the
            // persistance context open for the entire lifecycle of the method
            orderRepository.delete(order);
            throw ex;
        }

    }
}
