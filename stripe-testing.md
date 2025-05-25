# Stripe
To test the Stripe Webhook logic, the Stripe CLI is used.

## Setup Stripe

Install Stripe
```
brew install stripe/stripe-cli/stripe
```

Login to Stripe (follow the instructions to login)
```
stripe login
```

## Test Stripe Webhook

Tell Stripe where to send events too. Running this command will create a webhook signing secret which needs to be used for the `STRIPE_WEBHOOK_SECRET_KEY` in the .env file:
```
stripe listen --forward-to http://localhost:8080/checkout/webhook
```

Triggering a Stripe event
```
stripe trigger <event_type>
```

Triggering a Stripe event (actual event)
```
stripe trigger payment_intent.succeeded
```

## Event Types:
- `payment_intent.succeeded`
- `payment_intent.failed`


## Viewing Events (transactions)
https://dashboard.stripe.com/test/payments

## Checking the API version
Go to the following URL to check the latest version of the Stripe API:
[dashboard.stripe.com/test/developers](https://dashboard.stripe.com/test/developers)