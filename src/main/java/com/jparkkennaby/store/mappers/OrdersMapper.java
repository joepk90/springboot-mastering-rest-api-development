package com.jparkkennaby.store.mappers;

import org.mapstruct.Mapper;

import com.jparkkennaby.store.dtos.OrderDto;

import com.jparkkennaby.store.entities.Order;

@Mapper(componentModel = "spring") // so spring is made aware and can make beans at runtime
public interface OrdersMapper {
    OrderDto toDto(Order order);
}
