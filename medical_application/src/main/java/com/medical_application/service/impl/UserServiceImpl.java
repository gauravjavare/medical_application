package com.medical_application.service.impl;


import com.medical_application.entity.Appointment;
import com.medical_application.entity.Role;
import com.medical_application.entity.User;
import com.medical_application.exception.EntityNotFoundException;
import com.medical_application.payload.AppointmentDto;
import com.medical_application.payload.user.UpdateUserDto;
import com.medical_application.payload.user.UserDto;
import com.medical_application.repository.AppointmentRepository;
import com.medical_application.repository.RoleRepository;
import com.medical_application.repository.UserRepository;
import com.medical_application.service.UserService;
import com.medical_application.util.EntityDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepo;
    private final AppointmentRepository appointmentRepository;
    private final EntityDtoMapper entityDtoMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, RoleRepository roleRepo, AppointmentRepository appointmentRepository, EntityDtoMapper entityDtoMapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;
        this.appointmentRepository = appointmentRepository;
        this.entityDtoMapper = entityDtoMapper;
    }


    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserDto> dtos = users.stream().map(user -> this.entityDtoMapper.mapToDto(user,UserDto.class)).collect(Collectors.toList());
        return dtos;
    }

    @Override
    public UserDto getUserById(long userId) {

        User user = userRepository.findById(userId).orElseThrow(
                () -> new EntityNotFoundException("User Not find With Given ID " + userId));
        UserDto dto = entityDtoMapper.mapToDto(user,UserDto.class);
        return dto;
    }

    @Override
    public UserDto updateUser(UpdateUserDto newUserDetails) {
        User user = userRepository.findById(newUserDetails.getUserId()).orElseThrow(
                () -> new EntityNotFoundException("User does not exists with id " + newUserDetails.getUserId()));
        user.setPassword(passwordEncoder.encode(newUserDetails.getPassword()));
        Role role = roleRepo.findByName("ROLE_USER").orElseThrow(() -> new NoSuchElementException("No Roles found"));
        user.setRole(role.getName());
        user.setUserId(newUserDetails.getUserId());
        user.setName(newUserDetails.getName());
        user.setEmail(newUserDetails.getEmail());
        user.setMobile(newUserDetails.getMobile());
        User saveUser= userRepository.save(user);
        return entityDtoMapper.mapToDto(saveUser, UserDto.class);
    }
    @Override
    public void deleteUser(long userId) {

        userRepository.deleteById(userId);
    }

    @Override
    public List<AppointmentDto>getAppointmentByUserId(long userId) {
        List<Appointment> appByUserId = appointmentRepository.findByUserId(userId);
        return appByUserId.stream().map(appointment -> {
            AppointmentDto appointmentDto = new AppointmentDto();
            appointmentDto.setAppointmentId(appointment.getAppointmentId());
            appointmentDto.setDoctorId(appointment.getDoctor().getDoctorId());
            appointmentDto.setUserId(appointment.getUser().getUserId());
            appointmentDto.setAppointmentDateTime(appointment.getAppointmentDateTime());
            appointmentDto.setAvailable(appointment.isAvailable());
            return appointmentDto;
        }).collect(Collectors.toList());
    }
    }




