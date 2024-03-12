package com.medical_application.controller;

import com.medical_application.payload.AppointmentDto;
import com.medical_application.payload.doctor.DoctorWithTimeslot;
import com.medical_application.payload.user.UpdateUserDto;
import com.medical_application.payload.user.UserDto;
import com.medical_application.security.CustomUserDetails;
import com.medical_application.service.AppointmentService;
import com.medical_application.service.DoctorService;
import com.medical_application.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
   private final DoctorService doctorService;
    private final AppointmentService appointmentService;
    @Autowired
    public UserController(UserService userService, DoctorService doctorService, AppointmentService appointmentService) {
        this.userService = userService;
        this.doctorService = doctorService;
        this.appointmentService = appointmentService;
    }

    @GetMapping
   // @PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{userId}")
    //@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<UserDto> getUserById(@PathVariable long userId) {
        UserDto userDto = userService.getUserById(userId);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }

    @PutMapping("/{userId}")
    //@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<?> updateUser(@PathVariable long userId, @RequestBody UpdateUserDto newUserDetails, Authentication authentication) {
        long loggedInUserId = getLoggedInUserId(authentication);
        if (userId != loggedInUserId) {
            return new ResponseEntity<>("You can only update your own profile", HttpStatus.FORBIDDEN);
        }
//        UserDto existingUser = userService.getUserById(userId);
//        if (existingUser == null) {
//            return ResponseEntity.notFound().build();
//        }
        newUserDetails.setUserId(userId);
        UserDto userDto = userService.updateUser(newUserDetails);
        return new ResponseEntity<>(userDto, HttpStatus.OK);
    }
    private long getLoggedInUserId(Authentication authentication) {
        if (authentication.getPrincipal() instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            return userDetails.getUserId();
        }
        throw new IllegalStateException("User not properly authenticated.");
    }
    @DeleteMapping("/{userId}")
    //@PreAuthorize("hasRole('ROLE_USER')")
    public ResponseEntity<Void> deleteUser(@PathVariable long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
    @DeleteMapping("/delete/appointment")
    public ResponseEntity<?>deleteAppointment(@RequestParam long appointmentId ){
        appointmentService.deleteAppByUserByUsingAppId(appointmentId);
        return new ResponseEntity<>("Appointment is Deleted",HttpStatus.OK);
    }
    @GetMapping("/getAppointment/{userId}")
    public ResponseEntity<List<AppointmentDto>>getAppointmentByUserId(@PathVariable long userId){
        List<AppointmentDto> appointmentByUserId = userService.getAppointmentByUserId(userId);
        return new ResponseEntity<>(appointmentByUserId,HttpStatus.OK);
    }
    @PutMapping("/update/appointment")
    public ResponseEntity<?>updateAppointment(@RequestParam long appointmentId,
                                              @RequestBody AppointmentDto appointmentDto){
        appointmentDto.setAppointmentId(appointmentId);
        appointmentService.updateAppointment(appointmentDto);
        return new ResponseEntity<>("your Appointment is updated",HttpStatus.OK);
    }
    @GetMapping("/doctorTimeslots/{doctorId}")
    public ResponseEntity<DoctorWithTimeslot> getDoctorDetailsWithAvailableTimeSlots(@PathVariable long doctorId) {
        DoctorWithTimeslot doctorDetailsWithAvailableTimeSlots = doctorService.getDoctorDetailsWithAvailableTimeSlots(doctorId);
        return new ResponseEntity<>(doctorDetailsWithAvailableTimeSlots,HttpStatus.OK);
    }

}
