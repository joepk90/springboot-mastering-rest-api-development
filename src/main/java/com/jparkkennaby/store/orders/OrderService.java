package com.jparkkennaby.store.orders;

import org.springframework.security.access.AccessDeniedException;
import java.util.List;

import org.springframework.stereotype.Service;

import com.jparkkennaby.store.auth.AuthService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class OrderService {
    private final AuthService authService;
    private final OrderRepository orderRepository;
    private final OrdersMapper ordersMapper;

    public List<OrderDto> getAllOrders() {
        var user = authService.getCurrentUser();
        var orders = orderRepository.getOrdersByCustomer(user);
        return orders.stream().map(ordersMapper::toDto).toList();
    }

    public OrderDto getOrder(Long orderId) {
        var order = orderRepository.getOrderWithItems(orderId)
                .orElseThrow(OrderNotFoundException::new);

        var user = authService.getCurrentUser();

        if (!order.isPlacedBy(user)) {
            throw new AccessDeniedException("You don't have access to this order");
        }

        return ordersMapper.toDto(order);
    }
}
