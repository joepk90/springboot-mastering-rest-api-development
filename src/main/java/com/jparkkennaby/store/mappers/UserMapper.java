package com.jparkkennaby.store.mappers;

import org.mapstruct.Mapper;

import com.jparkkennaby.store.dtos.UserDto;
import com.jparkkennaby.store.entities.User;

@Mapper(componentModel = "spring") // so spring is made aware and can make beans at runtime
public interface UserMapper {
    UserDto toDto(User user);
}
