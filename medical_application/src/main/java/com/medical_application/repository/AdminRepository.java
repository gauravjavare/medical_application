package com.medical_application.repository;

import com.medical_application.entity.Admin;
import com.medical_application.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin,Long> {
    Optional<Admin> findByEmail(String email);
    Boolean existsByEmail(String email);
}
