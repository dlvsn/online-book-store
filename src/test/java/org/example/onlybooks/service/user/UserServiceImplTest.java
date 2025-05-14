package org.example.onlybooks.service.user;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;
import org.example.onlybooks.dto.user.RegisterUserRequestDto;
import org.example.onlybooks.dto.user.UserResponseDto;
import org.example.onlybooks.exception.RegistrationException;
import org.example.onlybooks.mapper.UserMapper;
import org.example.onlybooks.model.Role;
import org.example.onlybooks.model.User;
import org.example.onlybooks.repository.role.RoleRepository;
import org.example.onlybooks.repository.user.UserRepository;
import org.example.onlybooks.service.shoppingcart.ShoppingCartService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ShoppingCartService shoppingCartService;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("""
            Test that registering a new user 
            with a non-existing email is successful
            """)
    void registerNewUser_withNonExistingEmail_Success() throws RegistrationException {
        RegisterUserRequestDto registerUserRequestDto = initRequestDto();

        User user = initUser(registerUserRequestDto);

        when(userRepository.existsByEmail(registerUserRequestDto.getEmail())).thenReturn(false);

        when(userMapper.toModel(registerUserRequestDto)).thenReturn(user);

        when(passwordEncoder.encode(registerUserRequestDto.getPassword()))
                .thenReturn("Encoded password");

        Role role = initRole();

        when(roleRepository.findByName(Role.RoleName.ROLE_USER))
                .thenReturn(Optional.of(role));

        when(userRepository.save(user)).thenReturn(user);

        doNothing().when(shoppingCartService).registerNewShoppingCart(user);

        UserResponseDto expected = initResponseDto(user);
        when(userMapper.toDto(user)).thenReturn(expected);

        UserResponseDto actual = userService.register(registerUserRequestDto);

        assertThat(actual).isEqualTo(expected);
        verify(userRepository, times(1)).existsByEmail(registerUserRequestDto.getEmail());
        verify(userMapper, times(1)).toModel(registerUserRequestDto);
        verify(passwordEncoder, times(1)).encode(registerUserRequestDto.getPassword());
        verify(roleRepository, times(1)).findByName(Role.RoleName.ROLE_USER);
        verify(shoppingCartService, times(1)).registerNewShoppingCart(user);
        verify(userRepository, times(1)).save(user);
        verify(userMapper, times(1)).toDto(user);
        verifyNoMoreInteractions(userRepository, shoppingCartService, userMapper);
    }

    @Test
    @DisplayName("""
            Test that attempting to register 
            a new user with an already existing email throws an exception
            """)
    void registerNewUser_withExistingEmail_ThrowException() {
        RegisterUserRequestDto registerUserRequestDto = initRequestDto();
        when(userRepository.existsByEmail(registerUserRequestDto.getEmail())).thenReturn(true);
        Assertions.assertThrows(RegistrationException.class, () ->
                userService.register(registerUserRequestDto));
    }

    private RegisterUserRequestDto initRequestDto() {
        RegisterUserRequestDto registerUserRequestDto = new RegisterUserRequestDto();
        registerUserRequestDto.setEmail("test mail");
        registerUserRequestDto.setPassword("test password");
        registerUserRequestDto.setRepeatPassword(registerUserRequestDto.getPassword());
        registerUserRequestDto.setFirstName("first name");
        registerUserRequestDto.setLastName("last name");
        registerUserRequestDto.setShippingAddress(null);
        return registerUserRequestDto;
    }

    private UserResponseDto initResponseDto(User user) {
        return new UserResponseDto(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getShippingAddress()
        );
    }

    private Role initRole() {
        Role role = new Role();
        role.setId(1L);
        role.setName(Role.RoleName.ROLE_USER);
        return role;
    }

    private User initUser(RegisterUserRequestDto requestDto) {
        User user = new User();
        user.setEmail(requestDto.getEmail());
        user.setPassword(requestDto.getPassword());
        user.setFirstName(requestDto.getFirstName());
        user.setLastName(requestDto.getLastName());
        user.setShippingAddress(requestDto.getShippingAddress());
        return user;
    }
}
