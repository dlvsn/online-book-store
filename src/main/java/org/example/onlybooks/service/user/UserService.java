package org.example.onlybooks.service.user;

import org.example.onlybooks.dto.user.RegisterUserRequestDto;
import org.example.onlybooks.dto.user.UserResponseDto;
import org.example.onlybooks.exception.RegistrationException;

public interface UserService {
    UserResponseDto register(RegisterUserRequestDto userDto) throws RegistrationException;
}
