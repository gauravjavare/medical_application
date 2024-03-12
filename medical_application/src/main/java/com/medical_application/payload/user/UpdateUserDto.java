package com.medical_application.payload.user;

import lombok.Data;

@Data
public class UpdateUserDto {
    private long userId;
    private String email;
    private String name;
    private long mobile;
    private String password;
    private String role;
}
