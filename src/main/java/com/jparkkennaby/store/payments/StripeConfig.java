package com.jparkkennaby.store.payments;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    @Value("${stripe.secretKey}")
    private String secretKey;

    // post construct tells springboot to call this method when this bean is created
    // (when this class is initialised)
    @PostConstruct
    public void init() {
        Stripe.apiKey = secretKey;
    }
}
