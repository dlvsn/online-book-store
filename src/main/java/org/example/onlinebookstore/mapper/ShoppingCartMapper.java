package org.example.onlinebookstore.mapper;

import org.example.onlinebookstore.config.MapperConfig;
import org.example.onlinebookstore.dto.shoppingcart.ShoppingCartResponseDto;
import org.example.onlinebookstore.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class,
        uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user.id")
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);
}
