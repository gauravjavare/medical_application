package com.medical_application.payload.doctor;

import com.medical_application.payload.Timeslot;
import lombok.Data;

import java.util.List;

@Data
public class DoctorWithTimeslot {
    private long doctorId;
    private String doctorName; // Add any other doctor details you want to return
    private List<Timeslot> availableTimeSlots;
}
