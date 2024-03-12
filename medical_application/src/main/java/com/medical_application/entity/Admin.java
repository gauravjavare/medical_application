package com.medical_application.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Pattern;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "admins")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long adminId;
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@admin\\.com$", message = "Email must be a valid admin email")
    private String email;
    private String name;
    private long mobile;
    private String password;
    private String role;

}
