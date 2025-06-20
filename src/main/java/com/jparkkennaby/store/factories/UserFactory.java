package com.jparkkennaby.store.factories;

import org.springframework.stereotype.Component;

import com.jparkkennaby.store.entities.User;

import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * UserFactory
 * 
 * Sits as an abstract layer between the service and the entity, which injects
 * the passwordEncoder at instantiation.
 * 
 * Note: the passwordEncoder SHOULD not be injected into the entity
 */

@Component
public class UserFactory {
    private final PasswordEncoder passwordEncoder;

    public UserFactory(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public User setPassword(User user, String password) {
        user.setPassword(passwordEncoder.encode(password));
        return user;
    }
}