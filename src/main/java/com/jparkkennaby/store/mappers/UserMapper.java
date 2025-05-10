package com.jparkkennaby.store.mappers;

import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;

import com.jparkkennaby.store.dtos.RegisterUserRequest;
import com.jparkkennaby.store.dtos.UserDto;
import com.jparkkennaby.store.entities.User;

@Mapper(componentModel = "spring") // so spring is made aware and can make beans at runtime
public interface UserMapper {
    // @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);
    User toEntity(RegisterUserRequest request);
}
