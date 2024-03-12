package com.medical_application.controller;



import com.medical_application.entity.Appointment;
import com.medical_application.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @Autowired
    public AppointmentController(AppointmentService appointmentService) {
        this.appointmentService = appointmentService;
    }

    @PostMapping("/book")
    public ResponseEntity<?> bookAppointment(@RequestParam Long userId,
                                             @RequestParam Long doctorId,@RequestBody Appointment appointment) {
        try {
            Appointment bookedAppointment = appointmentService.bookAppointment(userId,doctorId,appointment);
            return new ResponseEntity<>(bookedAppointment,HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }
}
