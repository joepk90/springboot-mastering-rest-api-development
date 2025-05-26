package com.jparkkennaby.store.payments;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CheckoutRequest {
    @NotNull(message = "Cart ID is required.")
    private UUID cartId;
}
