package com.jparkkennaby.store.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.jparkkennaby.store.entities.User;
import com.jparkkennaby.store.repositories.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class AuthService {
    private UserRepository userRepository;

    public User getCurrentUser() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var userId = (Long) authentication.getPrincipal(); // user id set in filter

        // find by id is more efficient than by email
        return userRepository.findById(userId).orElse(null);
    }
}
