package com.medical_application.controller;


import com.medical_application.entity.Role;
import com.medical_application.entity.User;
import com.medical_application.payload.LoginDto;
import com.medical_application.payload.SignUpDto;
import com.medical_application.repository.AdminRepository;
import com.medical_application.repository.DoctorRepository;
import com.medical_application.repository.RoleRepository;
import com.medical_application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private DoctorRepository doctorRepo;
    @Autowired
    private AuthenticationManager authenticationManager;


    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new
                    UsernamePasswordAuthenticationToken(loginDto.getEmail(),
                    loginDto.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return new ResponseEntity<>("User signed-in successfully!.", HttpStatus.OK);
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>("Bad credentials", HttpStatus.UNAUTHORIZED);
        }
    }
    @PostMapping("/signup/user")
    public ResponseEntity<?> registerUser(@RequestBody SignUpDto signUpDto) {
        if (userRepo.existsByEmail(signUpDto.getEmail())) {
            return new ResponseEntity<>("Email Already Exists", HttpStatus.BAD_REQUEST);
        }
        User user = new User();
        user.setEmail(signUpDto.getEmail());
        user.setName(signUpDto.getName());
        user.setMobile(signUpDto.getMobile());
        user.setPassword(passwordEncoder.encode(signUpDto.getPassword()));
        Role role = roleRepo.findByName("ROLE_USER").orElseThrow(() -> new NoSuchElementException("No Roles found"));
        user.setRole(role.getName());
        userRepo.save(user);

        return new ResponseEntity<>("User is Registered Successfully", HttpStatus.OK);
    }
}

