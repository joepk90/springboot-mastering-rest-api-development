package com.jparkkennaby.store.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jparkkennaby.store.dtos.OrderDto;
import com.jparkkennaby.store.mappers.OrdersMapper;
import com.jparkkennaby.store.repositories.OrderRepository;
import com.jparkkennaby.store.services.AuthService;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrdersMapper ordersMapper;

    @GetMapping
    public List<OrderDto> getOrders() {
        var user = authService.getCurrentUser();

        var orders = orderRepository.findAllByCustomerId(user.getId());

        List<OrderDto> ordersList = new ArrayList<>();
        orders.forEach(order -> {
            ordersList.add(ordersMapper.toDto(order));
        });

        return ordersList;
    }

}
