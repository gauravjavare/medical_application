package com.medical_application.service.impl;

import com.medical_application.entity.Appointment;
import com.medical_application.entity.Doctor;
import com.medical_application.entity.User;
import com.medical_application.exception.EntityNotFoundException;
import com.medical_application.payload.AppointmentDto;
import com.medical_application.payload.Timeslot;
import com.medical_application.repository.AppointmentRepository;
import com.medical_application.repository.DoctorRepository;
import com.medical_application.repository.UserRepository;
import com.medical_application.service.AppointmentService;
import com.medical_application.service.DoctorService;
import com.medical_application.util.EntityDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserRepository userRepository;
    private final DoctorRepository doctorRepository;
    private  final DoctorService doctorService;
    private final EntityDtoMapper entityDtoMapper;
    private List<Timeslot> timeslots;
    @Autowired
    public AppointmentServiceImpl(AppointmentRepository appointmentRepository, UserRepository userRepository, DoctorRepository doctorRepository, DoctorService doctorService, EntityDtoMapper entityDtoMapper) {
        this.appointmentRepository = appointmentRepository;
         this.userRepository = userRepository;
        this.doctorRepository = doctorRepository;
        this.doctorService = doctorService;
        this.entityDtoMapper = entityDtoMapper;
    }
    @PostConstruct
    private void init() {
        timeslots = doctorService.initializeTimeSlots();
    }
    @Override
    public Appointment bookAppointment(Long userId, Long doctorId, Appointment appointment) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User not found with id " + userId));
        Doctor doctor = doctorRepository.findById(doctorId).orElseThrow(
                () -> new EntityNotFoundException("Doctor not found with id " + doctorId));
        appointment.setUser(user);
        appointment.setDoctor(doctor);
        if (isAppointmentValid(appointment, doctor)) {
            if (!isDoctorTimeslotBooked(doctor, appointment.getAppointmentDateTime())) {
                appointment.setAvailable(true);
                return appointmentRepository.save(appointment);
            } else {
                throw new IllegalArgumentException("Time slot is not available.");
            }
        } else {
            throw new EntityNotFoundException("Time slot is not available.");
        }
    }
    private boolean isAppointmentValid(Appointment appointment, Doctor doctor) {
        for (Timeslot timeslot : timeslots) {
            if (appointment.getAppointmentDateTime().isEqual(timeslot.getStartTime()) ||
                    (appointment.getAppointmentDateTime().isAfter(timeslot.getStartTime()) &&
                            appointment.getAppointmentDateTime().isBefore(timeslot.getEndTime()))) {
                return isTimeSlotAvailable(timeslot, doctor);
            }
        }
        return false;
    }

    private boolean isTimeSlotAvailable(Timeslot timeslot, Doctor doctor) {
        LocalDateTime startTime = timeslot.getStartTime();
        LocalDateTime endTime = timeslot.getEndTime();
        List<Timeslot> bookedTimeSlots = fetchDoctorBookedTimeSlotsFromDatabase(doctor);
        if (bookedTimeSlots != null) {
            for (Timeslot bookedSlot : bookedTimeSlots) {
                if (startTime.isBefore(bookedSlot.getEndTime()) && endTime.isAfter(bookedSlot.getStartTime())) {
                    return false; // Timeslot overlaps with a booked timeslot
                } else if (startTime.isEqual(bookedSlot.getStartTime()) || endTime.isEqual(bookedSlot.getEndTime())) {
                    return false; // Timeslot starts or ends exactly at the same time as a booked timeslot
                }
            }
        }
        return true;
    }
    private List<Timeslot> fetchDoctorBookedTimeSlotsFromDatabase(Doctor doctor) {
        // Fetch booked time slots from the BookingRepository for the specified doctor
        List<Appointment> bookedAppointments = appointmentRepository.findByDoctorDoctorId(doctor.getDoctorId());
        List<Timeslot> timeslots = new ArrayList<>();
        for (Appointment appointment : bookedAppointments) {
            timeslots.add(new Timeslot(appointment.getAppointmentDateTime(),appointment.getAppointmentDateTime().plusHours(1)));
        }
        return timeslots;
    }
    @Override
    public List<Timeslot> getAvailableTimeSlotsForDoctor(long doctorId) {
        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new EntityNotFoundException("Doctor not found with given Id " + doctorId));
        List<Timeslot> availableTimeSlots = new ArrayList<>();

        for (Timeslot timeslot : timeslots) {
            if (isTimeSlotAvailable(timeslot, doctor) && isDoctorAvailable(doctor, timeslot)) {
                availableTimeSlots.add(timeslot);
            }
        }
        List<Timeslot> bookedTimeSlots = appointmentRepository.findBookedTimeSlotsForDoctor(doctorId);
        if (bookedTimeSlots != null) {
            // Filter out any available timeslots that overlap with booked timeslots
            availableTimeSlots = availableTimeSlots.stream()
                    .filter(timeSlot -> bookedTimeSlots.stream()
                            .noneMatch(bookedSlot -> timeSlot.getStartTime().isBefore(bookedSlot.getEndTime()) &&
                                    timeSlot.getEndTime().isAfter(bookedSlot.getStartTime())))
                    .collect(Collectors.toList());
        }

        return availableTimeSlots;
    }
    private boolean isDoctorTimeslotBooked(Doctor doctor, LocalDateTime bookingDateTime) {
        List<Timeslot> bookedTimeSlots = appointmentRepository.findBookedTimeSlotsForDoctor(doctor.getDoctorId());
        if (bookedTimeSlots != null) {
            for (Timeslot timeslot : bookedTimeSlots) {
                // Check if the booking time is within 24 hours of any booked timeslot
                if (bookingDateTime.isAfter(timeslot.getStartTime()) && bookingDateTime.isBefore(timeslot.getEndTime().plusHours(24))) {
                    return true;
                }
            }
        }
        return false;
    }
    private boolean isDoctorAvailable(Doctor doctor, Timeslot timeSlot) {
        LocalDateTime startTime = timeSlot.getStartTime();
        LocalDateTime endTime = timeSlot.getEndTime();
        List<Timeslot> bookedTimeSlots = appointmentRepository.findBookedTimeSlotsForDoctor(doctor.getDoctorId());
        if (bookedTimeSlots != null) {
            for (Timeslot bookedSlot : bookedTimeSlots) {
                if (startTime.isBefore(bookedSlot.getEndTime()) && endTime.isAfter(bookedSlot.getStartTime())) {
                    return false; // Timeslot overlaps with a booked timeslot
                }
            }
        }
        return true;
    }
    @Override
    public List<AppointmentDto> getAllAppointments() {
        List<Appointment> appointments = appointmentRepository.findAll();
        return appointments.stream().map(appointment -> {
            AppointmentDto appointmentDto = new AppointmentDto();
            appointmentDto.setAppointmentId(appointment.getAppointmentId());
            appointmentDto.setDoctorId(appointment.getDoctor().getDoctorId());
            appointmentDto.setUserId(appointment.getUser().getUserId());
            appointmentDto.setAppointmentDateTime(appointment.getAppointmentDateTime());
            appointmentDto.setAvailable(appointment.isAvailable());
            return appointmentDto;
        }).collect(Collectors.toList());
    }


    @Override
    public List<AppointmentDto> getAppointmentsByDoctorId(long doctorId) {
        List<Appointment> appointmentsByDoctorId = appointmentRepository.findByDoctorDoctorId(doctorId);
        return appointmentsByDoctorId.stream().map(appointment -> {
            AppointmentDto appointmentDto = new AppointmentDto();
            appointmentDto.setAppointmentId(appointment.getAppointmentId());
            appointmentDto.setDoctorId(appointment.getDoctor().getDoctorId());
            appointmentDto.setUserId(appointment.getUser().getUserId());
            appointmentDto.setAppointmentDateTime(appointment.getAppointmentDateTime());
            appointmentDto.setAvailable(appointment.isAvailable());
            return appointmentDto;
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteAppByUserByUsingAppId(long appointmentId) {
        appointmentRepository.deleteById(appointmentId);
    }
    @Transactional
    @Override
    public AppointmentDto updateAppointment(AppointmentDto appointmentDto) {
        try {
            Appointment appointment = appointmentRepository.findById(appointmentDto.getAppointmentId())
                    .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
            Doctor doctor = doctorRepository.findById(appointment.getDoctor().getDoctorId())
                    .orElseThrow(() -> new EntityNotFoundException("Doctor not found"));
            User user = userRepository.findById(appointment.getUser().getUserId())
                    .orElseThrow(() -> new EntityNotFoundException("User not found"));
            appointment.setDoctor(doctor);
            appointment.setUser(user);
            appointment.setAppointmentDateTime(appointmentDto.getAppointmentDateTime());

            if (isAppointmentValid(appointment, doctor)) {
                if (!isDoctorTimeslotBooked(doctor, appointment.getAppointmentDateTime())) {
                    appointment.setAvailable(true);
                    Appointment updatedAppointment = appointmentRepository.save(appointment);
                    System.out.println("Appointment updated successfully.");
                    return entityDtoMapper.mapToDto(updatedAppointment, AppointmentDto.class);
                } else {
                    throw new IllegalArgumentException("Time slot is not available.");
                }
            } else {
                System.out.println("Appointment is not valid.");
                // Optionally, throw an exception or return a specific result indicating invalid appointment
            }
        } catch (EntityNotFoundException ex) {
            System.err.println("Entity not found: " + ex.getMessage());
            // Handle entity not found exception, e.g., log it or return a specific DTO indicating the error
        } catch (IllegalArgumentException ex) {
            System.err.println("Invalid argument: " + ex.getMessage());
            // Handle illegal argument exception, e.g., log it or return a specific DTO indicating the error
        } catch (Exception ex) {
            System.err.println("An unexpected error occurred: " + ex.getMessage());
            // Handle any unexpected exceptions
        }
        // In case of any exception, you might want to return a default value or indicate an error
        return null; // Or return a specific error DTO
    }

}
