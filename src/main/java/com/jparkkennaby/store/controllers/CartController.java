package com.jparkkennaby.store.controllers;

import java.util.Map;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
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
import com.jparkkennaby.store.dtos.AddItemToCartRequestDto;
import com.jparkkennaby.store.entities.Cart;
import com.jparkkennaby.store.mappers.CartMapper;
import com.jparkkennaby.store.services.CartService;
import com.jparkkennaby.store.repositories.CartRepository;
import com.jparkkennaby.store.repositories.ProductRepository;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;
    private final CartService cartService;

    @PostMapping
    public ResponseEntity<CartDto> createCart(UriComponentsBuilder uriBuilder) {
        var cartDto = cartService.createCart();
        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cartDto.getId()).toUri();

        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addToCart(
            @PathVariable UUID cartId,
            @RequestBody AddItemToCartRequestDto request,
            UriComponentsBuilder uriBuilder) {

        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        var product = productRepository.findById(request.getProductId()).orElse(null);
        if (product == null) {
            // return bad request because the issue is with the data the client has sent
            return ResponseEntity.badRequest().build();
        }

        var cartItem = cart.addItem(product);

        cartRepository.save(cart);

        var cartItemDto = cartMapper.toDto(cartItem);

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }

    @GetMapping("/{cartId}")
    public ResponseEntity<CartDto> getCart(
            @PathVariable UUID cartId,
            UriComponentsBuilder uriBuilder) {

        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        var cartDto = cartMapper.toDto(cart);

        return ResponseEntity.ok(cartDto);
    }

    @PutMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> updateItem(
            @PathVariable UUID cartId,
            @PathVariable Long productId,
            @Valid @RequestBody UpdateCartItemRequest request,
            UriComponentsBuilder uriBuilder) {

        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", "Cart not found."));
        }

        var cartItem = cart.getItem(productId);

        if (cartItem == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", "Product was not found in the cart."));
        }

        cartItem.setQuantity(request.getQuantity());
        cartRepository.save(cart);

        return ResponseEntity.ok(cartMapper.toDto(cartItem));
    }

    @DeleteMapping("/{cartId}/items/{productId}")
    public ResponseEntity<?> deleteItem(
            @PathVariable UUID cartId,
            @PathVariable Long productId) {

        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("error", "Cart not found."));
        }

        cart.removeItem(productId);

        cartRepository.save(cart);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{cartId}/items")
    public ResponseEntity<Void> clearCart(@PathVariable UUID cartId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        cart.clear();

        cartRepository.save(cart);

        return ResponseEntity.noContent().build();
    }
}
