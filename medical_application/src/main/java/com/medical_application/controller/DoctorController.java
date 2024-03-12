package com.medical_application.controller;



import com.medical_application.entity.Doctor;
import com.medical_application.entity.Role;
import com.medical_application.entity.review.Review;
import com.medical_application.exception.EntityNotFoundException;
import com.medical_application.payload.AppointmentDto;
import com.medical_application.payload.doctor.DoctorDto;
import com.medical_application.payload.review.ReviewDto;
import com.medical_application.repository.DoctorRepository;
import com.medical_application.repository.RoleRepository;
import com.medical_application.repository.reveiw.ReviewRepository;
import com.medical_application.service.AppointmentService;
import com.medical_application.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/doctor")
public class DoctorController {

    private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    private final DoctorRepository doctorRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReviewRepository reviewRepository;

    @Autowired
    public DoctorController(DoctorService doctorService, AppointmentService appointmentService, DoctorRepository doctorRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder, ReviewRepository reviewRepository) {
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
        this.doctorRepository = doctorRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.reviewRepository = reviewRepository;
    }
    @PostMapping("/signup")
    public ResponseEntity<?>registerDoctor(@RequestBody Doctor doctor){
        if(doctorRepository.existsByEmail(doctor.getEmail())){
            return new ResponseEntity<>("Email Already Exists", HttpStatus.BAD_REQUEST);
        }
        doctor.setPassword(passwordEncoder.encode(doctor.getPassword()));
        Role role = roleRepository.findByName("ROLE_DOCTOR").orElseThrow(() -> new NoSuchElementException("No Roles found"));
        doctor.setRole(role.getName());
        doctor.setStatus("pending");
        doctorRepository.save(doctor);
        return new ResponseEntity<>(("Doctor register Successfully"),HttpStatus.CREATED);
    }
    @GetMapping("/getAll")
    public ResponseEntity<List<DoctorDto>> getAllDoctors() {
        List<DoctorDto> allDoctors = doctorService.getAllApprovedDoctors();
        // Calculate and set average review for each doctor
        allDoctors.forEach(doctorDto -> {
            List<Review> reviews = reviewRepository.findByDoctorDoctorId(doctorDto.getDoctorId());
            doctorDto.setReview(calculateAverageReview(reviews));
        });
        return new ResponseEntity<>(allDoctors,HttpStatus.OK);
    }
    private double calculateAverageReview(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return 0.0;
        }
        double sum = reviews.stream().mapToInt(Review::getRating).sum();
        return sum / reviews.size();
    }
    @GetMapping("/get/{doctorId}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long doctorId) {
        try {
            DoctorDto doctorDto = doctorService.getApprovedDoctorById(doctorId);
            if (doctorDto == null) {
                throw new EntityNotFoundException("Doctor not found with id: " + doctorId);
            }
            // Fetch all reviews for the doctor
            List<Review> reviews = reviewRepository.findByDoctorDoctorId(doctorId);
            double averageReview = calculateAverageReview(reviews);
            doctorDto.setReview(averageReview); // Assuming you have a setter for averageReview
            return new ResponseEntity<>(doctorDto, HttpStatus.OK);
        } catch (Exception e) {
            // Handle other exceptions, if any
            return new ResponseEntity<>("An error occurred: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update/{doctorId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long doctorId, @RequestBody Doctor doctorDetails) {
        doctorDetails.setDoctorId(doctorId);
        Doctor updatedDoctor = doctorService.updateDoctor(doctorDetails);
        if (updatedDoctor != null) {
            return new ResponseEntity("your profile updated successfully",HttpStatus.OK);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping("/delete/{doctorId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long doctorId) {
        doctorService.deleteDoctor(doctorId);
        return new ResponseEntity<>("Doctor is deleted",HttpStatus.OK);
    }
//    @PostMapping("/addTimeSlot/{doctorId}")
//    @PreAuthorize("hasRole('ROLE_DOCTOR')")
//    public ResponseEntity<?> addTimeSlot(@PathVariable long doctorId, @RequestBody Set<Timeslot> timeslots, Authentication authentication) {
//        long loggedInDoctorId = getLoggedInDoctorId(authentication);
//        // Check if the timeslot's doctor ID matches the logged in doctor's ID
//        if (doctorId!= loggedInDoctorId) {
//            return new ResponseEntity<>("You can only add timeslots for yourself", HttpStatus.FORBIDDEN);
//        }
//        doctorService.saveTimeSlotForDr(doctorId,timeslots);
//        return new ResponseEntity<>("TimeSlot is added successfully", HttpStatus.OK);
//    }
//    private long getLoggedInDoctorId(Authentication authentication) {
//        if (authentication.getPrincipal() instanceof CustomDoctorDetails) {
//            CustomDoctorDetails doctorDetails = (CustomDoctorDetails) authentication.getPrincipal();
//            return doctorDetails.getDoctorId();
//        }
//        throw new IllegalStateException("Doctor not properly authenticated.");
//    }
//    @GetMapping("/getAllTimeslot/{doctorId}")
//    public ResponseEntity<Set<TimeslotDto>> getAllTimeslotByDoctorId(@PathVariable long doctorId){
//        Set<TimeslotDto> allTimeslotByDoctorId = doctorService.getAllTimeslotByDoctorId(doctorId);
//        return new ResponseEntity<>(allTimeslotByDoctorId,HttpStatus.OK);
//    }
    @GetMapping("/getAllAppointments/{doctorId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<List<AppointmentDto>> getAppointmentsByDoctorId(@PathVariable long doctorId){
        List<AppointmentDto> appointmentsByDoctorId = appointmentService.getAppointmentsByDoctorId(doctorId);
        return new ResponseEntity<>(appointmentsByDoctorId,HttpStatus.OK);
    }
    @GetMapping("/getAllReviewsForDr/{doctorId}")
    @PreAuthorize("hasRole('ROLE_DOCTOR')")
    public ResponseEntity<List<ReviewDto>> getAllReviewsForDoctor(@PathVariable long doctorId){
    List<ReviewDto> allReviewsForDoctor = doctorService.getAllReviewsForDoctor(doctorId);
    return new ResponseEntity<>(allReviewsForDoctor,HttpStatus.OK);
}
}



