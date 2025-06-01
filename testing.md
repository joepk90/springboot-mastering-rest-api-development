# REST API Usage/Testing Guide
How to use/test this REST API.

This document was written at the point of completion of the couse, and is subject to change.

To test the REST API either use Postman or API Docs generated with Swagger:
[localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

**TODO**
- Setup the Authorization Headers option in Swagger

## Generic Endpoints
Endpoints that are basic requirements of the API (Users, Products, Auth etc.).

### Products
The list of available Products can be viewed by anyone, but to create, update or delete a product you must be signed in as a user with the role of `admin`.

To create, read, update or delete a product, the followign endpoint can be used:
```
/products
```

### Users
Anyone can create a User by making a `POST` request to following endpoint 
```
/users
```

And provide a valid body object:
```
{
  "name": "string",
  "email": "string",
  "password": "string"
}
```


### Auth

**Logging In**

To authenticate (login) the `/auth/login` endpoint can be used:
```
/auth/login
```

Logging in using the credentials used to create a User, will return a `access_token`. This `access_token` can then be used to make authenticated requests to other endpoints. A refresh token is also returned as a cookie on the Response Header. This can be used by front end clients to request new a `access_token` .


**Who am I**

To see the user you are authenticated as, the folliwing endpoint can be used:
```
/auth/me
```

There is currenly no way to set a user role except by manually updating the database.

## Payment Endpoints
The following endpoints are used to create Carts, which are then used to create Rrders, and are ultiamtely converted into Stripe transactions.

To start the process of purchasing a product, a Cart must be created. Anyone can create a cart (authentication isn't requred).

### Cart

**Creating a Cart**

To create a Cart a `POST` request must be made to the `/carts` endpoint:
```
/carts
```

This will return a Cart response object:
```
{
    "id": "<CART_ID>",
    "items": [],
    "totalPrice": 0
}
```


**Requesting a Cart**

A cart record can be requested using the `GET` method to the following endpoint:
```
/carts/{{cartId}}
```


**Adding Items to the Cart**

Items can then be added to the Cart using the following endpoint:
```
/carts/<CART_ID>/items
```

A valid request body is required
```
{
"productId": <PRODUCT_ID>
}
```


**Clearing Items from the Cart**

And the items can be cleared using the delete method to::
```
/carts/<CART_ID>/items
```

<i>Note: this will empty the items in the cart, not delete the cart record</i>


**Updating or Deleting Items from the Cart**

Cart Items can then be updated or deleted the following endpoint:
```
/carts/<CART_ID>/items/<CART_ITEM_ID>
```

The quantity must be provided in the request body:
```
{
"quantity": INT
}
```

### Checkout

**Creating an Order**

Next, a Cart can be converted into an order. To create an Order, a `POST` request to Checkout endpoint is used. The cart ID must be provided in the request body.
```
/checkout
```

Once an Order is made, an Order record is saved to the Database with the Order's Status set `PENDING`. At this point the Stripe payment flow begins...


### Stripe Payment Flow
When an Order is created via the `/checkout` endpoint, a checkoutUrl is returned. For example: `https://checkout.stripe.com/c/pay/...`

At this point, a user could make the payment, however the API would have no idea about whether is succeeded or failed, as it would all take place within the Stripe ecosystem.

In order to for our API to know about the Stripe payments, an endpoint has been setup recieve  Webhook events. The endpoint has been added to the `CheckoutController` under the following path:
```
/checkout/webhook
```

This endpoint will recieve multiple events from Stripe, however we only care about the following events:
- `payment_intent.succeeded`
- `payment_intent.payment_failed`

The logic to handle these events is in the `StripePaymentGateway` class, and depending on the tpye of event received, ether the Order Status will be updated to `SUCCESS` or `FAILED`. If 

In order test this locally, the Stripe CLI is used and events are manually produced. See the [stripe-testing.md](https://github.com/joepk90/springboot-mastering-rest-api-development/blob/main/stripe-testing.md) document for details on how to do this

So see the Stripe transactions (events made from the CLI will also appear here), go to Stripes Payments page:
https://dashboard.stripe.com/test/payments


To better understand the Checkout flow, watch the [Payment Processing - Overview of the Checkout Process](https://members.codewithmosh.com/courses/spring-boot-mastering-apis/lectures/60964063) lecture.

All this takes place once a request has been made to the `/checkout endpoint`. If there is a payment exception, the application state will be reverted the existing Cart will be untouched. If the request passes, and an paymnet is made, the Cart will be cleared if items. To understand this logic better, see the `CheckoutService` class.

To make a payment the following details can be used:
- Card Number: `4242 4242 4242 4242`
- Expiration Date: Any date in the future
- CVV Code: Anything


### Orders

**Getting Orders**

To see a all the Orders a User has made, a `GET` request can be made to the Order endpoint:
```
/orders
```


**Getting a Specific Order**

To see a specific order, the following endpoint can be used using the `GET` method:
```
/orders/<ORDER_ID>
```

**Notes:**
These endpoints will only return Orders made by specific users. If an order is requested using the `<ORDER_ID>`, and it does not belong to the user making the request, the request will be rejected.

There is currenly no (admin) endpoint to see all Orders. 