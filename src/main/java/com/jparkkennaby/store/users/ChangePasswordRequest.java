package com.jparkkennaby.store.users;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String oldPassword;
    private String NewPassword;
}
