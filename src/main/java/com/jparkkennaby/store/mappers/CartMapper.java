package com.jparkkennaby.store.mappers;

import org.mapstruct.Mapper;

import com.jparkkennaby.store.dtos.CartDto;
import com.jparkkennaby.store.entities.Cart;

@Mapper(componentModel = "spring") // so spring is made aware and can make beans at runtime
public interface CartMapper {
    CartDto toDto(Cart cart);
}
