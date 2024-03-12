package com.medical_application.controller;

import com.medical_application.entity.*;
import com.medical_application.exception.EntityNotFoundException;
import com.medical_application.payload.admin.AdminDto;
import com.medical_application.payload.admin.AdminSignUpDto;
import com.medical_application.payload.AppointmentDto;
import com.medical_application.payload.review.ReviewDto;
import com.medical_application.repository.AdminRepository;
import com.medical_application.repository.DoctorRepository;
import com.medical_application.repository.RoleRepository;
import com.medical_application.security.CustomAdminDetails;
import com.medical_application.service.AdminService;
import com.medical_application.service.AppointmentService;
import com.medical_application.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
   private final DoctorService doctorService;
   private final DoctorRepository doctorRepo;
   private final AdminRepository adminRepo;
   private final RoleRepository roleRepo;
   private final PasswordEncoder passwordEncoder;
   private final AppointmentService appointmentService;
    @Autowired
    public AdminController(AdminService adminService, DoctorService doctorService, DoctorRepository doctorRepo, AdminRepository adminRepo, RoleRepository roleRepo, PasswordEncoder passwordEncoder, AppointmentService appointmentService) {
        this.adminService = adminService;
        this.doctorService = doctorService;
        this.doctorRepo = doctorRepo;
        this.adminRepo = adminRepo;
        this.roleRepo = roleRepo;
        this.passwordEncoder = passwordEncoder;
        this.appointmentService = appointmentService;
    }
    @PostMapping("/signup")
    public ResponseEntity<?> registerAdmin(@RequestBody AdminSignUpDto dto) {
        if (adminRepo.existsByEmail(dto.getEmail())) {
            return new ResponseEntity<>("Email Already Exists", HttpStatus.BAD_REQUEST);
        }
        Admin admin = new Admin();
        admin.setEmail(dto.getEmail());
        admin.setName(dto.getName());
        admin.setMobile(dto.getMobile());
        admin.setPassword(passwordEncoder.encode(dto.getPassword()));
        Role role = roleRepo.findByName("ROLE_ADMIN").orElseThrow(() -> new NoSuchElementException("No Roles found"));
        admin.setRole(role.getName());
        adminRepo.save(admin);

        return new ResponseEntity<>("Admin is Registered Successfully", HttpStatus.OK);
    }
    @PostMapping("/register/doctor")
    public ResponseEntity<?> SaveDoctor(@RequestBody Doctor doctor){
        if(doctorRepo.existsByEmail(doctor.getEmail())){
            return new ResponseEntity<>("Email Already Exists", HttpStatus.BAD_REQUEST);
        }
        doctor.setPassword(passwordEncoder.encode(doctor.getFirstName()+"@123"));
        doctor.setStatus("approved");
        doctor.setRole("ROLE_DOCTOR");
        Doctor saved = doctorRepo.save(doctor);
        return new ResponseEntity<>("Doctor is register Successfully",HttpStatus.CREATED );
    }
    @PutMapping("/update/admin/{adminId}")
    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateAdmin(@PathVariable long adminId, @RequestBody Admin newAdminData, Authentication authentication) {
        long loggedInAdminId = getLoggedInAdminId(authentication);
        if (adminId != loggedInAdminId) {
            return new ResponseEntity<>("You can only update your own profile", HttpStatus.FORBIDDEN);
        }
        newAdminData.setAdminId(adminId);
        Admin updatedAdmin = adminService.updateAdmin(newAdminData);
        return new ResponseEntity<>("Your profile is updated!!", HttpStatus.OK);
    }

    private long getLoggedInAdminId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomAdminDetails) {
            CustomAdminDetails adminDetails = (CustomAdminDetails) authentication.getPrincipal();
            return adminDetails.getAdminId();
        }
        throw new IllegalStateException("Admin not properly authenticated.");
    }



    @DeleteMapping("/delete/{adminId}")
   // @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteAdmin(@PathVariable long adminId) {
        try {
            adminService.deleteAdmin(adminId);
            return new ResponseEntity<>("Admin deleted successfully", HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/get/{adminId}")
   // @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<AdminDto> getAdminById(@PathVariable long adminId) {
        AdminDto adminDto = adminService.getAdminById(adminId);
        return new ResponseEntity<>(adminDto, adminDto != null ? HttpStatus.OK : HttpStatus.NOT_FOUND);
    }

    @GetMapping("/all")
   // @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<List<AdminDto>> getAllAdmins() {
        List<AdminDto> adminDtos = adminService.getAllAdmins();
        return new ResponseEntity<>(adminDtos, HttpStatus.OK);
    }

    @PatchMapping("/doctor/{doctorId}/approve")
   // @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> approveDoctorStatus(@PathVariable Long doctorId) {
        try {
            Doctor doctor = doctorRepo.findById(doctorId).orElseThrow(
                    () -> new EntityNotFoundException("Doctor not found with id "+doctorId));
            if  (doctor== null) {
                return ResponseEntity.notFound().build();
            }
            doctor.setStatus("approved");
            doctorRepo.save(doctor);
            return ResponseEntity.ok("Doctor status updated to approved.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    @PutMapping("/update/user/{userId}")
   public ResponseEntity<String> updateUser(@PathVariable long userId, @RequestBody User newUser) {
        newUser.setUserId(userId);
        User user = adminService.updateUser(newUser);
        if (user != null) {
            return new ResponseEntity<>("user is updated Successfully", HttpStatus.OK);
        }
        return new ResponseEntity<>("User not found",HttpStatus.BAD_REQUEST);
    }

    @GetMapping("/getAllAppointments")
    public ResponseEntity<List<AppointmentDto>> getAllAppointments(){
        List<AppointmentDto> appointmentDtos = appointmentService.getAllAppointments();
        return new ResponseEntity<>(appointmentDtos,HttpStatus.OK);
    }
    @DeleteMapping("/deleteReview/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable long reviewId){
        adminService.deleteReview(reviewId);
        return new ResponseEntity<>("review is deleted",HttpStatus.OK);
    }

    @GetMapping("/getAllReviews")
    public ResponseEntity<List<ReviewDto>> allReviews(){
        List<ReviewDto> allReviews = adminService.getAllReviews();
        return new ResponseEntity<>(allReviews,HttpStatus.OK);
    }
}