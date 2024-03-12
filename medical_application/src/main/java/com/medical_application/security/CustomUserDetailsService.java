package com.medical_application.security;

import com.medical_application.entity.Admin;
import com.medical_application.entity.Doctor;
import com.medical_application.entity.User;
import com.medical_application.repository.AdminRepository;
import com.medical_application.repository.DoctorRepository;
import com.medical_application.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private AdminRepository adminRepo;
    @Autowired
    private DoctorRepository doctorRepo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userRepo.findByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
            return new CustomUserDetails(user);
        } catch (UsernameNotFoundException e) {
            try {
                Admin admin = adminRepo.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Admin not found: " + username));
                return new CustomAdminDetails(admin);
            } catch (UsernameNotFoundException ex) {
                Doctor doctor = doctorRepo.findByEmail(username)
                        .orElseThrow(() -> new UsernameNotFoundException("Doctor not found: " + username));
                return new CustomDoctorDetails(doctor);
            }
        }
    }
}
