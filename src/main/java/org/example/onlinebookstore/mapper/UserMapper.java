package org.example.onlinebookstore.mapper;

import org.example.onlinebookstore.config.MapperConfig;
import org.example.onlinebookstore.dto.user.RegisterUserRequestDto;
import org.example.onlinebookstore.dto.user.UserResponseDto;
import org.example.onlinebookstore.model.User;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface UserMapper {
    UserResponseDto toDto(User user);

    User toModel(RegisterUserRequestDto userDto);
}
