package com.medical_application.payload.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private long userId;
    private String email;
    private String name;
    private long mobile;

}

