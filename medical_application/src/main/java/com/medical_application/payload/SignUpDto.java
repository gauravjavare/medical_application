package com.medical_application.payload;

import lombok.Data;

@Data
public class SignUpDto {
    private String email;
    private String name;
    private long mobile;
    private String password;
    private String role;
}
