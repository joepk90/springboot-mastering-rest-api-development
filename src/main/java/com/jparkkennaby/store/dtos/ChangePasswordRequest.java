package com.jparkkennaby.store.dtos;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String NewPassword;
}
