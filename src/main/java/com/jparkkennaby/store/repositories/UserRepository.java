package com.jparkkennaby.store.repositories;

import org.springframework.data.repository.CrudRepository;

import com.jparkkennaby.store.entities.User;

public interface UserRepository extends CrudRepository<User, Long> {
}
