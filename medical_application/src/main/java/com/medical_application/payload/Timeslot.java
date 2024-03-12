package com.medical_application.payload;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Timeslot {
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}

