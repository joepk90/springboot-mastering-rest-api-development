package com.jparkkennaby.store.payments;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.jparkkennaby.store.entities.Order;
import com.jparkkennaby.store.entities.OrderItem;
import com.jparkkennaby.store.entities.PaymentStatus;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import com.stripe.param.checkout.SessionCreateParams;

import jakarta.servlet.http.HttpServletRequest;

@Service
public class StripePaymentGateway implements PaymentGateway {

    // TODO: create endpoints for the success/cancel urls?
    // private String checkoutSuccessUrl = "checkout-success";
    // private String checkoutCancelUrl = "checkout-success";

    private String checkoutSuccessUrl = "/swagger-ui/index.html";
    private String checkoutCancelUrl = "/swagger-ui/index.html";

    // colon (:) means default to empty string
    @Value("${websiteUrl:}")
    private String websiteUrl;

    @Value("${stripe.webhookSecretKey}")
    private String webhookSecretKey;

    // hacky solution to send the user back to the api url.
    // in real world applciation, there would be a seperate front end we could
    // direct the user to.
    public String getBaseUrl() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                .getRequest();

        String scheme = request.getScheme(); // http / https
        String protocol = scheme + "://";
        String serverName = request.getServerName(); // your-service.run.app or custom domain

        // localhost
        if (serverName.equals("localhost")) {
            int port = request.getServerPort(); // 443 / 80 / other
            return protocol + serverName + ":" + port;
        }

        // real domain
        return protocol + serverName;
    }

    public String getWebsiteUrl() {
        // if website url is set in in the application yaml file use it.
        // otherwise create the url dynamically from the request
        if (websiteUrl.equals("")) {
            return getBaseUrl();
        }

        return websiteUrl;
    }

    @Override
    public CheckoutSession createCheckoutSession(Order order) {
        var websiteUrl = getWebsiteUrl();

        try {
            // create checkout session
            var builder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(websiteUrl + "/" + checkoutSuccessUrl + "?orderId=" + order.getId())
                    .setCancelUrl(websiteUrl + "/" + checkoutCancelUrl)
                    .setPaymentIntentData(createPaymentIntent(order));

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

    public SessionCreateParams.PaymentIntentData createPaymentIntent(Order order) {
        return SessionCreateParams.PaymentIntentData.builder()
                .putMetadata("order_id", order.getId().toString()).build();
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
            throw new PaymentException("Could not deserialise Stripe event. CHeck the SDK and API version.");
        }
    }

    @Override
    public Optional<PaymentResult> parseWebhookRequest(WebhookRequest request) {
        try {
            var payload = request.getPayload();
            var signature = request.getHeaders().get("stripe-signature");
            var event = Webhook.constructEvent(payload, signature, webhookSecretKey);

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

            return switch (event.getType()) {
                case "payment_intent.succeeded" ->
                    Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.PAID));
                case "payment_intent.payment_failed" ->
                    Optional.of(new PaymentResult(extractOrderId(event), PaymentStatus.FAILED));
                default -> Optional.empty();
            };

        } catch (SignatureVerificationException e) {
            throw new PaymentException("Invalid Signature");
        }
    }

    private Long extractOrderId(Event event) {
        // custom code (not in course)
        var stripeObject = getStripeObject(event);

        var paymentIntent = (PaymentIntent) stripeObject;
        return Long.valueOf(paymentIntent.getMetadata().get("order_id"));
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
