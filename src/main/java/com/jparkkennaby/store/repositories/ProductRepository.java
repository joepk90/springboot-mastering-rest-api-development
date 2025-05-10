package com.jparkkennaby.store.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jparkkennaby.store.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Byte categoryId);
}