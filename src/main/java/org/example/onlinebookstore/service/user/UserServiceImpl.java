package org.example.onlinebookstore.service.user;

import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.example.onlinebookstore.dto.user.RegisterUserRequestDto;
import org.example.onlinebookstore.dto.user.UserResponseDto;
import org.example.onlinebookstore.exception.EntityNotFoundException;
import org.example.onlinebookstore.exception.RegistrationException;
import org.example.onlinebookstore.mapper.UserMapper;
import org.example.onlinebookstore.model.Role;
import org.example.onlinebookstore.model.User;
import org.example.onlinebookstore.repository.role.RoleRepository;
import org.example.onlinebookstore.repository.user.UserRepository;
import org.example.onlinebookstore.service.shoppingcart.ShoppingCartService;
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
