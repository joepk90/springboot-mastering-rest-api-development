package com.jparkkennaby.store.repositories;

import org.springframework.data.repository.CrudRepository;

import com.jparkkennaby.store.entities.Category;

public interface CategoryRepository extends CrudRepository<Category, Byte> {
}