package com.medical_application.payload.admin;

import lombok.Data;

import javax.validation.constraints.Pattern;

@Data
public class AdminSignUpDto {
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@admin\\.com$", message = "Email must be a valid admin email")
    private String email;
    private String name;
    private long mobile;
    private String password;
    private String role;
}
