package com.jparkkennaby.store.repositories;

import org.springframework.data.repository.CrudRepository;

import com.jparkkennaby.store.entities.Address;

public interface AddressRepository extends CrudRepository<Address, Long> {
}