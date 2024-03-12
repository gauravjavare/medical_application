package com.medical_application.payload.admin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDto {
    private long adminId;
    private String email;
    private String name;
    private long mobile;
}
