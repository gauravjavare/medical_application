package com.medical_application.repository;


import com.medical_application.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DoctorRepository extends JpaRepository<Doctor,Long> {

    Optional<Doctor> findByEmail(String email);
    Boolean existsByEmail(String email);


}
