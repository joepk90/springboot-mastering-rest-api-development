package com.jparkkennaby.store.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.jparkkennaby.store.dtos.CartDto;
import com.jparkkennaby.store.dtos.CartItemDto;
import com.jparkkennaby.store.entities.Cart;
import com.jparkkennaby.store.entities.CartItem;

@Mapper(componentModel = "spring") // so spring is made aware and can make beans at runtime
public interface CartMapper {
    @Mapping(target = "totalPrice", expression = "java(cart.getTotalPrice())")
    CartDto toDto(Cart cart);

    @Mapping(target = "totalPrice", expression = "java(cartItem.getTotalPrice())")
    CartItemDto toDto(CartItem cartItem);
}
