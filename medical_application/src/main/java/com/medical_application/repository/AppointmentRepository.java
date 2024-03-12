package com.medical_application.repository;

import com.medical_application.entity.Appointment;
import com.medical_application.entity.Doctor;
import com.medical_application.payload.Timeslot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

public interface AppointmentRepository extends JpaRepository<Appointment,Long> {

//    @Query("SELECT b FROM Appointment b WHERE b.appointmentDateTime IN :timeslots")
//    List<Appointment> findByAppointmentDateTimeIn(@Param("timeslots") Set<Date> timeslots);
//
//    // Custom method to fetch booked time slots for a doctor
//    @Query("SELECT b.appointmentDateTime FROM Appointment b WHERE b.doctorId = :doctorId AND b.isAvailable = false")
//    Set<Timeslot> findBookedTimeSlotsForDoctor(@Param("doctorId") long doctorId);


    @Query("SELECT a.appointmentDateTime FROM Appointment a WHERE a.doctor.doctorId = :doctorId AND a.isAvailable = false")
    List<Timeslot> findBookedTimeSlotsForDoctor(@Param("doctorId") Long doctorId);



    List<Appointment> findByDoctorDoctorId(Long doctorId);


    @Query("SELECT a FROM Appointment a WHERE a.user.userId = :userId")
    List<Appointment> findByUserId(Long userId);
}
