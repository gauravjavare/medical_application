package com.medical_application.service;


import com.medical_application.entity.Admin;
import com.medical_application.entity.User;
import com.medical_application.payload.admin.AdminDto;
import com.medical_application.payload.review.ReviewDto;

import java.util.List;

public interface AdminService {

    Admin updateAdmin(Admin newAdminData);
    void deleteAdmin(long adminId);
    AdminDto getAdminById(long adminId);
    List<AdminDto> getAllAdmins();

    User updateUser(User newUser);

    void deleteReview(long reviewId);

    List<ReviewDto> getAllReviews();
}

