package com.jparkkennaby.store.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

import com.jparkkennaby.store.dtos.UserDto;
import com.jparkkennaby.store.entities.User;

@Mapper(
    componentModel = "spring", // so spring is made aware and can make beans at runtime
    unmappedTargetPolicy = ReportingPolicy.IGNORE // ignores unmapped fields
) 
public interface UserMapper {
    UserDto toDto(User user);
}
