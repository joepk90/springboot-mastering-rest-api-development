package com.jparkkennaby.store.users;

import org.mapstruct.Mapper;
// import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring") // so spring is made aware and can make beans at runtime
public interface UserMapper {
    // @Mapping(target = "createdAt", expression =
    // "java(java.time.LocalDateTime.now())")
    UserDto toDto(User user);

    User toEntity(RegisterUserRequest request);

    void update(UpdateUserRequest request, @MappingTarget User user);
}
