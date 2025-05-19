package com.jparkkennaby.store.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.jparkkennaby.store.dtos.JwtResponse;
import com.jparkkennaby.store.dtos.LoginRequest;
import com.jparkkennaby.store.dtos.UserDto;
import com.jparkkennaby.store.mappers.UserMapper;
import com.jparkkennaby.store.repositories.UserRepository;
import com.jparkkennaby.store.services.JwtService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import lombok.AllArgsConstructor;

@RestController
@AllArgsConstructor
@RequestMapping("/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping("/me")
    public ResponseEntity<UserDto> me() {
        // auth context is set in the JwtAuthenticationFilter during http request
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        var email = (String) authentication.getPrincipal(); // email set in filter

        var user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        var userDto = userMapper.toDto(user);
        return ResponseEntity.ok(userDto);

    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@Valid @RequestBody LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()));

        // exception should never be reached, because the authentication manager will
        // have already thrown an exception if the user doesn't exist or is invalid
        var user = userRepository.findByEmail(request.getEmail()).orElseThrow();

        var token = jwtService.generateToken(user);

        return ResponseEntity.ok(new JwtResponse(token));
    }

    @PostMapping("/validate")
    public boolean validate(@RequestHeader("Authorization") String authHeader) {
        // print intentionally committed to show filter functionality (LoggingFilter)
        System.out.println("Validate called");

        var token = authHeader.replace("Bearer ", "");
        return jwtService.validateToken(token);
    }

    // updates the response status to 401 (instead a 403 forbidden is returned)
    // when the user is not found or the password is incorrect
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Void> handleBadCredentialsException() {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}
