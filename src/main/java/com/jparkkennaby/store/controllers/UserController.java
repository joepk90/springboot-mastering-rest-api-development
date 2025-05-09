package com.jparkkennaby.store.controllers;

import com.jparkkennaby.store.dtos.UserDto;
import com.jparkkennaby.store.repositories.UserRepository;

import lombok.AllArgsConstructor;

// import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;

    // GetMapping just handles the GET method
    @GetMapping
    public Iterable<UserDto> getAllUsers() {
        return userRepository.findAll()
            .stream()
            .map(user -> new UserDto(user.getId(), user.getName(), user.getEmail()))
            .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        var user = userRepository.findById(id).orElse(null);
        if (user == null) {
            // return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            return ResponseEntity.notFound().build();
        }
        
         // return new ResponseEntity<>(user, HttpStatus.OK);
        var userDto =  new UserDto(user.getId(), user.getName(), user.getEmail());
         return ResponseEntity.ok(userDto);
    }
}
