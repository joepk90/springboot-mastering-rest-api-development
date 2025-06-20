package com.jparkkennaby.store.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.jparkkennaby.store.dtos.CartDto;
import com.jparkkennaby.store.dtos.CartItemDto;
import com.jparkkennaby.store.dtos.UpdateCartItemRequest;
import com.jparkkennaby.store.entities.Cart;
import com.jparkkennaby.store.entities.CartItem;
import com.jparkkennaby.store.annoations.MaxTableSizeCheck;
import com.jparkkennaby.store.dtos.AddItemToCartRequestDto;
import com.jparkkennaby.store.exceptions.CartNotFoundException;
import com.jparkkennaby.store.products.ProductNotFoundException;
import com.jparkkennaby.store.services.CartService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
@Tag(name = "Carts") // controls the name of the controller in swagger
public class CartController {
    private final CartService cartService;

    @MaxTableSizeCheck(entity = Cart.class)
    @PostMapping
    public ResponseEntity<CartDto> createCart(UriComponentsBuilder uriBuilder) {
        var cartDto = cartService.createCart();
        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();

        return ResponseEntity.created(uri).body(cartDto);
    }

    @MaxTableSizeCheck(entity = CartItem.class)
    @PostMapping("/{cartId}/items")
    @Operation(summary = "Adds a product to the cart.")
    public ResponseEntity<CartItemDto> addToCart(
            @Parameter(description = "The ID of the cart") @PathVariable UUID cartId,
            @RequestBody AddItemToCartRequestDto request) {
        var cartItemDto = cartService.addToCart(cartId, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }

    @GetMapping("/{cartId}")
    public CartDto getCart(@PathVariable UUID cartId) {
        return cartService.getCart(cartId);
    }

    @PutMapping("/{cartId}/items/{productId}")
    public CartItemDto updateItem(
            @PathVariable UUID cartId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateitem(cartId, productId, request.getQuantity());
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> removeItem(
            @PathVariable UUID cartId,
            @PathVariable Long productId) {
        cartService.removeItem(cartId, productId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> clearCart(@PathVariable UUID cartId) {
        cartService.clearCart(cartId);
        return ResponseEntity.noContent().build();
    }

    // returned to the client if the CartNotFoundException exception is thrown (see
    // the CartService)
    @ExceptionHandler(CartNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleCartNotFound() {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                Map.of("error", "Cart not found."));
    }

    // returned to the client if the CartNotFoundException exception is thrown (see
    // the CartService)
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleProductNotFound() {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of("error", "Product not found in the cart."));
    }
}
