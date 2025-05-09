package org.example.onlinebookstore.service.user;

import org.example.onlinebookstore.dto.user.RegisterUserRequestDto;
import org.example.onlinebookstore.dto.user.UserResponseDto;
import org.example.onlinebookstore.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(RegisterUserRequestDto userDto) throws RegistrationException;
}
