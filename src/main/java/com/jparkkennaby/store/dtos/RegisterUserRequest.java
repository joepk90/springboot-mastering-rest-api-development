package com.jparkkennaby.store.dtos;

import lombok.Data;

@Data // equivalent to Getter, Setter, toString and toHashCode
public class RegisterUserRequest {
    private String name;
    private String email;
    private String password;

}
