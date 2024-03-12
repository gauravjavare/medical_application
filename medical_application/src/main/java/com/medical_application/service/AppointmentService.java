package com.medical_application.service;

import com.medical_application.entity.Appointment;
import com.medical_application.payload.AppointmentDto;
import com.medical_application.payload.Timeslot;

import java.util.List;

public interface AppointmentService {

    Appointment bookAppointment(Long userId, Long doctorId, Appointment appointment);
    List<Timeslot> getAvailableTimeSlotsForDoctor(long doctorId);

    List<AppointmentDto> getAllAppointments();
    List<AppointmentDto> getAppointmentsByDoctorId(long doctorId);

    void deleteAppByUserByUsingAppId(long appointmentId);

    AppointmentDto updateAppointment(AppointmentDto appointmentDto);
}
