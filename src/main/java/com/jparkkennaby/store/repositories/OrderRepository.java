package com.jparkkennaby.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jparkkennaby.store.entities.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {

}
