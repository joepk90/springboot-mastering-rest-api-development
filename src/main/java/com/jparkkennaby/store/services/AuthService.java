package com.jparkkennaby.store.services;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jparkkennaby.store.dtos.LoginRequest;
import com.jparkkennaby.store.exceptions.IncorrectPasswordException;
import com.jparkkennaby.store.exceptions.UserNotFoundException;
import com.jparkkennaby.store.repositories.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void login(LoginRequest loginRequest) {
        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        var user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            throw new UserNotFoundException();
        }

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IncorrectPasswordException();
        }
    }
}
