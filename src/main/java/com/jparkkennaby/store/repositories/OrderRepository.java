package com.jparkkennaby.store.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jparkkennaby.store.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findAllByCustomerId(Long customerId);
}
