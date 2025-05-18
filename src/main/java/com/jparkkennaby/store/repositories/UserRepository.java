package com.jparkkennaby.store.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jparkkennaby.store.entities.User;

/**
 * UserRepository:
 * 
 * - Uses a JpaRepository instead of CrudRepository so that the findAll method returns
 * a List, instead of an Iterable object.
 * - Returning a List allows us to use the stream api to map User objects to the UserDto object.
 */

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);
}
