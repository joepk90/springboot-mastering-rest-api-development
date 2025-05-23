package com.jparkkennaby.store.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.jparkkennaby.store.dtos.OrderDto;
import com.jparkkennaby.store.mappers.OrdersMapper;
import com.jparkkennaby.store.repositories.OrderRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class OrderService {
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrdersMapper ordersMapper;

    public List<OrderDto> getAllOrders() {
        var user = authService.getCurrentUser();
        var orders = orderRepository.findAllByCustomer(user);
        return orders.stream().map(ordersMapper::toDto).toList();
    }
}
