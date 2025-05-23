package com.jparkkennaby.store.controllers;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.jparkkennaby.store.dtos.CheckoutRequest;
import com.jparkkennaby.store.dtos.CheckoutResponse;
import com.jparkkennaby.store.dtos.ErrorDto;
import com.jparkkennaby.store.entities.Order;
import com.jparkkennaby.store.entities.OrderItem;
import com.jparkkennaby.store.entities.OrderStatus;
import com.jparkkennaby.store.repositories.CartRepository;
import com.jparkkennaby.store.repositories.OrderRepository;
import com.jparkkennaby.store.services.AuthService;
import com.jparkkennaby.store.services.CartService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/checkout")
public class CheckoutController {
    private CartRepository cartRepository;
    private CartService cartService;
    private OrderRepository orderRepository;
    private AuthService authService;

    @PostMapping
    public ResponseEntity<?> checkout(@Valid @RequestBody CheckoutRequest request) {
        var cart = cartRepository.getCartWithItems(request.getCartId()).orElse(null);
        if (cart == null) {
            return ResponseEntity.badRequest().body(
                    new ErrorDto("Cart not found"));
        }

        if (cart.getItems().isEmpty()) {
            return ResponseEntity.badRequest().body(
                    new ErrorDto("Cart is empty"));
        }

        var order = new Order();
        order.setCustomer(authService.getCurrentUser());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalPrice(cart.getTotalPrice());

        cart.getItems().forEach(item -> {
            var orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setTotalPrice(item.getTotalPrice());
            orderItem.setUnitPrice(item.getProduct().getPrice());
            order.getItems().add(orderItem);
        });

        orderRepository.save(order);
        cartService.clearCart(cart.getId());

        return ResponseEntity.ok(new CheckoutResponse(order.getId()));
    }
}
