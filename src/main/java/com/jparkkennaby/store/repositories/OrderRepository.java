package com.jparkkennaby.store.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jparkkennaby.store.entities.Order;
import com.jparkkennaby.store.entities.User;

public interface OrderRepository extends JpaRepository<Order, Long> {
    public List<Order> findAllByCustomer(User customer);
}
