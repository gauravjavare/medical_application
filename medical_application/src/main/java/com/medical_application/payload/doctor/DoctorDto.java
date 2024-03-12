package com.medical_application.payload.doctor;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoctorDto {

    private Long doctorId;
    private String firstName;
    private String lastName;
    private String email;
    private long mobile;
    private String specialization;
    private Integer experience;
    private String qualification;
    private String workingHours;
    private Boolean isActive;
    private String status;
    private String hospitalOrClinic;
    private double review;

}



