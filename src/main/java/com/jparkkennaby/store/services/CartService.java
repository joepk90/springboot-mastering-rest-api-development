package com.jparkkennaby.store.services;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.jparkkennaby.store.dtos.CartDto;
import com.jparkkennaby.store.dtos.CartItemDto;
import com.jparkkennaby.store.entities.Cart;
import com.jparkkennaby.store.exceptions.CartNotFoundException;
import com.jparkkennaby.store.exceptions.ProductNotFoundException;
import com.jparkkennaby.store.mappers.CartMapper;
import com.jparkkennaby.store.repositories.CartRepository;
import com.jparkkennaby.store.repositories.ProductRepository;

@Service
public class CartService {
    private CartRepository cartRepository;
    private ProductRepository productRepository;
    private CartMapper cartMapper;

    public CartDto createCart() {
        var cart = new Cart();
        cartRepository.save(cart);
        return cartMapper.toDto(cart);
    }

    public CartItemDto addToCart(UUID cartId, Long productId) {
        var cart = cartRepository.getCartWithItems(cartId).orElse(null);
        if (cart == null) {
            throw new CartNotFoundException();
        }

        var product = productRepository.findById(productId).orElse(null);
        if (product == null) {
            // return bad request because the issue is with the data the client has sent
            throw new ProductNotFoundException();
        }

        var cartItem = cart.addItem(product);

        cartRepository.save(cart);

        return cartMapper.toDto(cartItem);
    }

}
