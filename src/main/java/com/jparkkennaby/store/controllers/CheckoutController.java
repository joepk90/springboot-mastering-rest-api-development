package com.jparkkennaby.store.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jparkkennaby.store.dtos.CheckoutRequest;
import com.jparkkennaby.store.dtos.CheckoutResponse;
import com.jparkkennaby.store.dtos.ErrorDto;
import com.jparkkennaby.store.entities.OrderStatus;
import com.jparkkennaby.store.exceptions.CartEmptyException;
import com.jparkkennaby.store.exceptions.CartNotFoundException;
import com.jparkkennaby.store.exceptions.PaymentException;
import com.jparkkennaby.store.repositories.OrderRepository;
import com.jparkkennaby.store.services.CheckoutService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import com.stripe.exception.EventDataObjectDeserializationException;

// RequiredArgsConstructor - spring will only initialise the fields declared as final

@RestController
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;
    private final OrderRepository orderRepository;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    @PostMapping
    public CheckoutResponse checkout(@Valid @RequestBody CheckoutRequest request) {
        return checkoutService.checkout(request);
    }

    // custom code to deserialize the event to a StripeObject
    public StripeObject getStripeObject(Event event) {
        // Get the deserializer for the inner data object
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();

        // isPresent always returned false, to resolve this
        // the deserializeUnsafe function (below) has been used
        if (deserializer.getObject().isPresent()) {
            // logic used in course (not working)
            return event.getDataObjectDeserializer().getObject().orElse(null);
        }

        try {
            return deserializer.deserializeUnsafe();
        } catch (EventDataObjectDeserializationException e) {
            // Handle the exception and return null or handle accordingly
            return null;
        }
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> handleWebhook(
            @RequestHeader("Stripe-Signature") String signiture,
            @RequestBody String payload) {
        try {
            var event = Webhook.constructEvent(payload, signiture, webhookSecretKey);
            System.out.println(event.getType());

            // custom code (not in course)
            StripeObject stripeObject = getStripeObject(event);

            /**
             * StripeObject is an Abstract Class
             * 
             * StripeObject is the base class for the all the modern classes in Stripe
             * - charge
             * - refund
             * - customer
             * 
             * Depending on the type of event, we need to cast the stripe object to a type
             * that is more specific.
             * 
             * Examples of casting the event types:
             * - charge -> (Charge) stripeObject
             * - payment_intent.succeeded -> (PaymentIntent) stripeObject
             */

            switch (event.getType()) {
                case "payment_intent.succeeded" -> {
                    var paymentIntent = (PaymentIntent) stripeObject;
                    if (paymentIntent != null) {
                        var orderId = paymentIntent.getMetadata().get("order_id");
                        var order = orderRepository.findById(Long.valueOf(orderId)).orElseThrow();
                        order.setStatus(OrderStatus.PAID);
                        orderRepository.save(order);
                    }
                }
                case "payment_intent.failed" -> {
                    // update order status (FAILED)
                }
            }

            return ResponseEntity.ok().build();

        } catch (SignatureVerificationException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // custom exception handler (not in course)
    @ExceptionHandler(EventDataObjectDeserializationException.class)
    public ResponseEntity<ErrorDto> handlestripeDeserializationException(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }

    @ExceptionHandler(PaymentException.class)
    public ResponseEntity<?> handlePaymentException() {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorDto("Error creating a checkout session."));
    }

    @ExceptionHandler({ CartNotFoundException.class, CartEmptyException.class })
    public ResponseEntity<ErrorDto> handhandleException(Exception ex) {
        return ResponseEntity.badRequest().body(new ErrorDto(ex.getMessage()));
    }
}
