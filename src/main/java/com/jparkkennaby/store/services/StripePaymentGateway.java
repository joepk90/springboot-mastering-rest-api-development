package com.jparkkennaby.store.services;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.jparkkennaby.store.entities.Order;
import com.jparkkennaby.store.entities.OrderItem;
import com.jparkkennaby.store.exceptions.PaymentException;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

@Service
public class StripePaymentGateway implements PaymentGateway {
    @Value("${websiteUrl}")
    private String websiteUrl;

    @Override
    public CheckoutSession createCheckoutSession(Order order) {
        try {
            // create checkout session
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/checkout-success?orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "/checkout-cancel")
                    .putMetadata("order_id", order.getId().toString());

            order.getItems().forEach(item -> {
                var lineItem = createLineItem(item);
                builder.addLineItem(lineItem);
            });

            var session = Session.create(builder.build());
            return new CheckoutSession(session.getUrl());
        } catch (StripeException ex) {
            // the exception should be logged using a external logging service (i.e. sentry)
            System.out.println(ex.getMessage());
            throw new PaymentException();
        }

    }

    private SessionCreateParams.LineItem createLineItem(OrderItem item) {
        return SessionCreateParams.LineItem.builder()
                .setQuantity(Long.valueOf(item.getQuantity()))
                .setPriceData(createPriceData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData createPriceData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmountDecimal(
                        item.getUnitPrice().multiply(BigDecimal.valueOf(100)))
                .setProductData(createProductData(item))
                .build();
    }

    private SessionCreateParams.LineItem.PriceData.ProductData createProductData(OrderItem item) {
        return SessionCreateParams.LineItem.PriceData.ProductData.builder()
                .setName(item.getProduct().getName())
                .build();
    }

}
