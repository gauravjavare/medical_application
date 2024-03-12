package com.medical_application.service.impl;

import com.medical_application.entity.Admin;
import com.medical_application.entity.Role;
import com.medical_application.entity.User;
import com.medical_application.entity.review.Review;
import com.medical_application.payload.admin.AdminDto;
import com.medical_application.payload.review.ReviewDto;
import com.medical_application.repository.AdminRepository;
import com.medical_application.repository.RoleRepository;
import com.medical_application.repository.UserRepository;
import com.medical_application.repository.reveiw.ReviewRepository;
import com.medical_application.service.AdminService;
import com.medical_application.util.EntityDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class AdminServiceImpl implements AdminService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
   private final EntityDtoMapper entityDtoMapper;
   private final UserRepository userRepository;
   private final ReviewRepository reviewRepository;
    @Autowired
    public AdminServiceImpl(AdminRepository adminRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepository, EntityDtoMapper entityDtoMapper, UserRepository userRepository, ReviewRepository reviewRepository) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.entityDtoMapper = entityDtoMapper;
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Admin updateAdmin(Admin newAdminData) {
        adminRepository.findById(newAdminData.getAdminId()).orElseThrow(
                () -> new EntityNotFoundException("Admin not found with this id " + newAdminData.getAdminId()));
        newAdminData.setPassword(passwordEncoder.encode(newAdminData.getPassword()));
        Role role = roleRepository.findByName("ROLE_ADMIN").orElseThrow(() -> new NoSuchElementException("No Roles found"));
        newAdminData.setRole(role.getName());
        return adminRepository.save(newAdminData);
    }

    @Override
    public void deleteAdmin(long adminId) {
        if (!adminRepository.existsById(adminId)) {
            throw new NoSuchElementException("Admin not found with id: " + adminId);
        }
        adminRepository.deleteById(adminId);
    }

    @Override
    public AdminDto getAdminById(long adminId) {
        Admin admin = adminRepository.findById(adminId).orElseThrow(()-> new EntityNotFoundException("Admin not found with id "+adminId));
        AdminDto adminDto = entityDtoMapper.mapToDto(admin, AdminDto.class);
        return adminDto;
    }

    @Override
    public List<AdminDto> getAllAdmins() {
        List<Admin> admins = adminRepository.findAll();
        List<AdminDto> adminDtos = admins.stream().map(admin -> entityDtoMapper.mapToDto(admin, AdminDto.class)).collect(Collectors.toList());
        return adminDtos;
    }

    @Override
    public User updateUser(User newUser) {
        userRepository.findById(newUser.getUserId()).orElseThrow(
                ()-> new EntityNotFoundException("User not found with id "+newUser.getUserId()));
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        Role role = roleRepository.findByName("ROLE_USER").orElseThrow(() -> new NoSuchElementException("No Roles found"));
        newUser.setRole(role.getName());
        return userRepository.save(newUser);
    }

    @Override
    public void deleteReview(long reviewId) {
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public List<ReviewDto> getAllReviews() {
        List<Review> allReviews = reviewRepository.findAll();
        List<ReviewDto> dtos = allReviews.stream().map((review ->
                entityDtoMapper.mapToDto(review, ReviewDto.class))).collect(Collectors.toList());
         return dtos;
    }
}

