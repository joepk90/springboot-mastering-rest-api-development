package com.jparkkennaby.store.controllers;

import com.jparkkennaby.store.dtos.UserDto;
import com.jparkkennaby.store.mappers.UserMapper;
import com.jparkkennaby.store.repositories.UserRepository;

import lombok.AllArgsConstructor;

import java.util.Set;

import org.springframework.data.domain.Sort;
// import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // GetMapping just handles the GET method
    @GetMapping
    public Iterable<UserDto> getAllUsers(
        @RequestParam(
            required = false,
            defaultValue = "",
            name = "sort"
        ) String sortBy
    ) {
        
        // restrict allowed sort values
        if (!Set.of("name", "email").contains(sortBy))
            sortBy = "name"; // defult sort paramter = name

        return userRepository.findAll(Sort.by(sortBy))
            .stream()
            .map(userMapper::toDto)
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
         return ResponseEntity.ok(userMapper.toDto(user));
    }
}
