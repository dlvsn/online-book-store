package org.example.onlybooks.mapper;

import org.example.onlybooks.config.MapperConfig;
import org.example.onlybooks.dto.user.RegisterUserRequestDto;
import org.example.onlybooks.dto.user.UserResponseDto;
import org.example.onlybooks.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(RegisterUserRequestDto userDto);
}
