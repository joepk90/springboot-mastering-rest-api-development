package com.jparkkennaby.store.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jparkkennaby.store.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}