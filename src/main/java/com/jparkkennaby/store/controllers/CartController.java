package com.jparkkennaby.store.controllers;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.jparkkennaby.store.dtos.CartDto;
import com.jparkkennaby.store.dtos.CartItemDto;
import com.jparkkennaby.store.dtos.AddItemToCartRequestDto;
import com.jparkkennaby.store.entities.Cart;
import com.jparkkennaby.store.entities.CartItem;
import com.jparkkennaby.store.mappers.CartMapper;
import com.jparkkennaby.store.repositories.CartRepository;
import com.jparkkennaby.store.repositories.ProductRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final CartMapper cartMapper;

    @PostMapping
    public ResponseEntity<CartDto> createCart(UriComponentsBuilder uriBuilder) {
        var cart = cartRepository.save(new Cart());
        var cartDto = cartMapper.toDto(cart);

        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cart.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }

    @PostMapping("/{cartId}/items")
    public ResponseEntity<CartItemDto> addToCart(
            @PathVariable UUID cartId,
            @RequestBody AddItemToCartRequestDto request,
            UriComponentsBuilder uriBuilder) {

        var cart = cartRepository.findById(cartId).orElse(null);
        if (cart == null) {
            return ResponseEntity.notFound().build();
        }

        var product = productRepository.findById(request.getProductId()).orElse(null);
        if (product == null) {
            // return bad request because the issue is with the data the client has sent
            return ResponseEntity.badRequest().build();
        }

        var cartItem = cart.getCartItems().stream()
                .filter(item -> item.getProduct().getId().equals(product.getId()))
                .findFirst().orElse(null);

        if (cartItem != null) {
            cartItem.setQuantity(cartItem.getQuantity() + 1);
        } else {
            cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cartItem.setQuantity(1);

            cart.getCartItems().add(cartItem);
        }

        cartRepository.save(cart);

        var cartItemDto = cartMapper.toDto(cartItem);

        return ResponseEntity.status(HttpStatus.CREATED).body(cartItemDto);
    }
}
