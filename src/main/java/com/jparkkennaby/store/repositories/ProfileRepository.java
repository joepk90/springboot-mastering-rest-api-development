package com.jparkkennaby.store.repositories;

import org.springframework.data.repository.CrudRepository;

import com.jparkkennaby.store.entities.Profile;

public interface ProfileRepository extends CrudRepository<Profile, Long> {
}