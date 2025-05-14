package org.example.onlybooks.service.user;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.onlybooks.dto.user.RegisterUserRequestDto;
import org.example.onlybooks.dto.user.UserResponseDto;
import org.example.onlybooks.exception.EntityNotFoundException;
import org.example.onlybooks.exception.RegistrationException;
import org.example.onlybooks.mapper.UserMapper;
import org.example.onlybooks.model.Role;
import org.example.onlybooks.model.User;
import org.example.onlybooks.repository.role.RoleRepository;
import org.example.onlybooks.repository.user.UserRepository;
import org.example.onlybooks.service.shoppingcart.ShoppingCartService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final ShoppingCartService shoppingCartService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Transactional
    @Override
    public UserResponseDto register(RegisterUserRequestDto userDto) throws RegistrationException {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new RegistrationException("User with email "
                    + userDto.getEmail()
                    + " already exists");
        }
        User newUser = userMapper.toModel(userDto);
        newUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        Role roleUser = roleRepository.findByName(Role.RoleName.ROLE_USER).orElseThrow(() ->
                new EntityNotFoundException("Can't find role " + Role.RoleName.ROLE_USER));
        newUser.setRoles(new HashSet<>(Set.of(roleUser)));
        userRepository.save(newUser);
        shoppingCartService.registerNewShoppingCart(newUser);
        return userMapper.toDto(newUser);
    }
}
