package com.medical_application.service;


import com.medical_application.payload.AppointmentDto;
import com.medical_application.payload.user.UpdateUserDto;
import com.medical_application.payload.user.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAllUsers();

    UserDto getUserById(long userId);

    UserDto updateUser(UpdateUserDto newUserDetails);

    void deleteUser(long userId);

    List<AppointmentDto> getAppointmentByUserId(long userId);
}
