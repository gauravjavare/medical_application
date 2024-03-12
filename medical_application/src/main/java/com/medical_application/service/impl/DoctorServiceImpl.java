package com.medical_application.service.impl;



import com.medical_application.entity.Doctor;
import com.medical_application.entity.Role;
import com.medical_application.entity.review.Review;
import com.medical_application.exception.EntityNotFoundException;
import com.medical_application.payload.doctor.DoctorDto;
import com.medical_application.payload.doctor.DoctorWithTimeslot;
import com.medical_application.payload.Timeslot;
import com.medical_application.payload.review.ReviewDto;
import com.medical_application.repository.DoctorRepository;
import com.medical_application.repository.RoleRepository;
import com.medical_application.repository.reveiw.ReviewRepository;
import com.medical_application.service.AppointmentService;
import com.medical_application.service.DoctorService;
import com.medical_application.util.EntityDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final DoctorRepository doctorRepository;
    private final EntityDtoMapper entityDtoMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final AppointmentService appointmentService;
    private final ReviewRepository reviewRepository;
    @Autowired
    public DoctorServiceImpl(DoctorRepository doctorRepository, EntityDtoMapper entityDtoMapper, PasswordEncoder passwordEncoder, RoleRepository roleRepository, @Lazy AppointmentService appointmentService, ReviewRepository reviewRepository) {
        this.doctorRepository = doctorRepository;
        this.entityDtoMapper = entityDtoMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.appointmentService = appointmentService;
        this.reviewRepository = reviewRepository;
    }
   @Override
   public List<Timeslot> initializeTimeSlots() {
       List<Timeslot> timeSlots = new ArrayList<>();
       LocalDate today = LocalDate.now(); // Use current date
       LocalTime startTime = LocalTime.of(10, 0);
       LocalTime endTime = LocalTime.of(18, 0);

       while (startTime.isBefore(endTime)) {
           LocalDateTime startDateTime = LocalDateTime.of(today, startTime);
           LocalDateTime endDateTime = startDateTime.plusHours(1);
           timeSlots.add(new Timeslot(startDateTime, endDateTime));
           startTime = startTime.plusHours(1);
       }
       return timeSlots;
   }

    @Override
    public List<DoctorDto> getAllApprovedDoctors() {
        List<Doctor> doctors = doctorRepository.findAll();
        return doctors.stream()
                .filter(doctor -> "approved".equals(doctor.getStatus()))
                .map(doctor -> entityDtoMapper.mapToDto(doctor, DoctorDto.class))
                .collect(Collectors.toList());
    }
    @Override
    public DoctorDto getApprovedDoctorById(Long doctorId) {
        Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);
        if (!doctorOptional.isPresent()) {
            throw new EntityNotFoundException("Doctor not found with id " + doctorId);
        }
        Doctor doctor = doctorOptional.get();
        if ("approved".equals(doctor.getStatus())) {
            return entityDtoMapper.mapToDto(doctor, DoctorDto.class);
        } else {
            return null;
        }
    }
    @Override
    public Doctor updateDoctor(Doctor doctorDetails) {
        doctorRepository.findById(doctorDetails.getDoctorId()).orElseThrow(
                () -> new EntityNotFoundException("Doctor not found with id " + doctorDetails.getDoctorId()));
        doctorDetails.setPassword(passwordEncoder.encode(doctorDetails.getPassword())); // Ensure to hash the password
        Role role = roleRepository.findByName("ROLE_DOCTOR").orElseThrow(() -> new NoSuchElementException("No Roles found"));
        doctorDetails.setRole(role.getName());
        doctorDetails.setStatus("pending");
        return doctorRepository.save(doctorDetails);
    }

    @Override
    public void deleteDoctor(Long doctorId) {
        doctorRepository.deleteById(doctorId);
    }

    @Override
    public DoctorWithTimeslot getDoctorDetailsWithAvailableTimeSlots(long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with given Id " + doctorId));

        List<Timeslot> availableTimeSlots = appointmentService.getAvailableTimeSlotsForDoctor(doctorId);

        DoctorWithTimeslot doctorWithTimeSlot = new DoctorWithTimeslot();
        doctorWithTimeSlot.setDoctorId(doctor.getDoctorId());
        doctorWithTimeSlot.setDoctorName(doctor.getFirstName()+" "+doctor.getLastName()); // You can add more doctor details as needed
        doctorWithTimeSlot.setAvailableTimeSlots(availableTimeSlots);

        return doctorWithTimeSlot;
    }

    @Override
    public List<ReviewDto> getAllReviewsForDoctor(long doctorId) {
        List<Review> reviews = reviewRepository.findByDoctorDoctorId(doctorId);
        List<ReviewDto> dtos = reviews.stream().map((review -> entityDtoMapper.mapToDto(review, ReviewDto.class)))
                .collect(Collectors.toList());
      return dtos;
    }


}




