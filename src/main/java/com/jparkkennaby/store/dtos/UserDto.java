package com.jparkkennaby.store.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Data Transfer Objets (DTO)
 * 
 * DTO's are used to manage the fields we want to expose in our public facing REST API
 */

@Getter
@AllArgsConstructor
public class UserDto {
    private Long id;
    private String name;
    private String email;
}
