package com.jparkkennaby.store.carts;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemToCartRequestDto {
    @NotNull
    private Long productId;
}
