package org.example.onlybooks.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.onlybooks.dto.user.LoginUserRequestDto;
import org.example.onlybooks.dto.user.LoginUserResponseDto;
import org.example.onlybooks.dto.user.RegisterUserRequestDto;
import org.example.onlybooks.dto.user.UserResponseDto;
import org.example.onlybooks.exception.RegistrationException;
import org.example.onlybooks.security.AuthenticationService;
import org.example.onlybooks.service.user.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Authentication",
        description = "Endpoints for managing user authentication and registration")
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final UserService userService;
    private final AuthenticationService authenticationService;

    @PostMapping("/login")
    public LoginUserResponseDto login(@RequestBody @Valid LoginUserRequestDto userDto) {
        return authenticationService.authenticate(userDto);
    }

    @Operation(summary = "Register new user. "
            + "checks whether such a user already exists, and checks "
            + "the validity of the email and password")
    @PostMapping("/register")
    UserResponseDto register(@RequestBody @Valid RegisterUserRequestDto userDto)
            throws RegistrationException {
        return userService.register(userDto);
    }
}
