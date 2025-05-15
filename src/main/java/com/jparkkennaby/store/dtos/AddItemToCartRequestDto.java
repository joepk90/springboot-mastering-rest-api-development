package com.jparkkennaby.store.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddItemToCartRequestDto {
    @NotNull
    private Long productId;
}
