package com.jparkkennaby.store.repositories;

import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import com.jparkkennaby.store.entities.Message;

import org.springframework.data.jpa.repository.JpaRepository;

@RepositoryRestResource
public interface MessagesRepository extends JpaRepository<Message, Long> {}