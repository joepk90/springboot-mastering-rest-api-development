package com.jparkkennaby.store.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.jparkkennaby.store.dtos.CartDto;
import com.jparkkennaby.store.entities.Cart;
import com.jparkkennaby.store.mappers.CartMapper;
import com.jparkkennaby.store.repositories.CartRepository;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/carts")
public class CartController {
    private final CartRepository cartRepository;
    private final CartMapper cartMapper;

    @PostMapping
    public ResponseEntity<CartDto> createCart(UriComponentsBuilder uriBuilder) {
        var cart = cartRepository.save(new Cart());
        var cartDto = cartMapper.toDto(cart);

        var uri = uriBuilder.path("/carts/{id}").buildAndExpand(cart.getId()).toUri();
        return ResponseEntity.created(uri).body(cartDto);
    }
}
