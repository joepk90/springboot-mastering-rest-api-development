package com.jparkkennaby.store.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.jparkkennaby.store.entities.Order;
import com.jparkkennaby.store.entities.User;

// get prefix is used to reprent custom loading of extra objects
// note: get does not actually affect the query logic, just used to represent customisation

public interface OrderRepository extends JpaRepository<Order, Long> {
    // use entity graph to eagily load the orders items and the related product
    @EntityGraph(attributePaths = "items.product")
    @Query("SELECT o FROM Order o WHERE o.customer = :customer")
    public List<Order> getOrdersByCustomer(@Param("customer") User customer);

    @EntityGraph(attributePaths = "items.product")
    @Query("SELECT o FROM Order o WHERE o.id = :orderId")
    public Optional<Order> getOrderWithItems(@Param("orderId") Long orderId);
}
