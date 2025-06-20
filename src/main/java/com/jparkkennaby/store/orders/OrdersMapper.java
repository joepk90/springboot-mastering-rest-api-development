package com.jparkkennaby.store.orders;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring") // so spring is made aware and can make beans at runtime
public interface OrdersMapper {
    OrderDto toDto(Order order);
}
