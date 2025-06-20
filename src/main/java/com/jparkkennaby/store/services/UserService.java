package com.jparkkennaby.store.services;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jparkkennaby.store.dtos.ChangePasswordRequest;
import com.jparkkennaby.store.dtos.RegisterUserRequest;
import com.jparkkennaby.store.dtos.UpdateUserRequest;
import com.jparkkennaby.store.dtos.UserDto;
import com.jparkkennaby.store.entities.Role;
import com.jparkkennaby.store.entities.User;
import com.jparkkennaby.store.exceptions.DuplicateUserException;
import com.jparkkennaby.store.exceptions.UserNotFoundException;
import com.jparkkennaby.store.factories.UserFactory;
import com.jparkkennaby.store.mappers.UserMapper;
import com.jparkkennaby.store.repositories.UserRepository;

import java.util.Set;

@AllArgsConstructor
@Service
public class UserService {
    private final UserFactory userFactory;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public Iterable<UserDto> getAllUsers(String sortBy) {
        // restrict allowed sort values
        if (!Set.of("name", "email").contains(sortBy))
            sortBy = "name";

        return userRepository.findAll(Sort.by(sortBy))
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDto getUser(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        return userMapper.toDto(user);
    }

    public UserDto registerUser(RegisterUserRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateUserException();
        }

        var user = userMapper.toEntity(request);
        user = userFactory.setPassword(user, user.getPassword());

        // setting the role could be handled in our user mapper,
        // but it's very important so we are setting it explicitly here
        user.setRole(Role.USER);

        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public UserDto updateUser(Long userId, UpdateUserRequest request) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        userMapper.update(request, user); // mutates the user object
        userRepository.save(user);

        return userMapper.toDto(user);
    }

    public void deleteUser(Long userId) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);
        userRepository.delete(user);
    }

    public void changePassword(Long userId, ChangePasswordRequest request) {
        var user = userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AccessDeniedException("Password does not match");
        }

        user = userFactory.setPassword(user, request.getNewPassword());
        userRepository.save(user);
    }
}