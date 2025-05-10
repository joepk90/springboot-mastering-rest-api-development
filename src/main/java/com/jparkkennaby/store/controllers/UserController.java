package com.jparkkennaby.store.controllers;

import com.jparkkennaby.store.dtos.RegisterUserRequest;
import com.jparkkennaby.store.dtos.UpdateUserRequest;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
// import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@AllArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    // GetMapping just handles the GET method
    @GetMapping
    public Iterable<UserDto> getAllUsers(
        // @RequestHeader(required = false, name = "x-auth-token") String authToken,
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

    @PostMapping
    public ResponseEntity<UserDto> createUser(
        @RequestBody RegisterUserRequest request,
        UriComponentsBuilder uriBuilder
        ) {
        var user = userMapper.toEntity(request);
        userRepository.save(user);

        var userDto = userMapper.toDto(user);

        // works, however does not follow standard REST conventions (returns 200 and no uri)
        // return ResponseEntity.ok(userDto);

        // created returns a 201 and a URI (where the resource was saved too -see respone headers)
        var uri = uriBuilder.path("/users/{id}").buildAndExpand(userDto.getId()).toUri();
        return ResponseEntity.created(uri).body(userDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(
            @PathVariable(name = "id") Long id,
            @RequestBody UpdateUserRequest request
        ) {

        var user = userRepository.findById(id).orElse(null);

        if (user == null) {
            return ResponseEntity.notFound().build();
        }

        userMapper.update(request, user); // mutates the user object
        userRepository.save(user);

        return ResponseEntity.ok(userMapper.toDto(user));
    }
}
