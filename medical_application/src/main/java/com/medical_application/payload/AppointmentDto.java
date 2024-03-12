package com.medical_application.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AppointmentDto {

    private long appointmentId;
    private long doctorId;
    private long userId;
    private LocalDateTime appointmentDateTime;
    private boolean isAvailable;

}
