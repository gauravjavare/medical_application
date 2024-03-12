package com.medical_application.service;

import com.medical_application.entity.Doctor;
import com.medical_application.payload.doctor.DoctorDto;
import com.medical_application.payload.doctor.DoctorWithTimeslot;
import com.medical_application.payload.Timeslot;
import com.medical_application.payload.review.ReviewDto;

import java.util.List;

public interface DoctorService {

    List<DoctorDto> getAllApprovedDoctors();

    DoctorDto getApprovedDoctorById(Long doctorId);

    Doctor updateDoctor(Doctor doctorDetails);

    void deleteDoctor(Long doctorId);
    List<Timeslot> initializeTimeSlots();
    DoctorWithTimeslot getDoctorDetailsWithAvailableTimeSlots(long doctorId);
    List<ReviewDto> getAllReviewsForDoctor(long doctorId);
}
