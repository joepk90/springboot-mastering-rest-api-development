package com.jparkkennaby.store.services;

import org.springframework.stereotype.Service;

import com.jparkkennaby.store.dtos.CartDto;
import com.jparkkennaby.store.entities.Cart;
import com.jparkkennaby.store.mappers.CartMapper;
import com.jparkkennaby.store.repositories.CartRepository;

@Service
public class CartService {
    private CartRepository cartRepository;
    private CartMapper cartMapper;

    public CartDto createCart() {
        var cart = new Cart();
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

}
