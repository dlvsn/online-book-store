package org.example.onlybooks.mapper;

import org.example.onlybooks.config.MapperConfig;
import org.example.onlybooks.dto.shoppingcart.ShoppingCartResponseDto;
import org.example.onlybooks.model.ShoppingCart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class,
        uses = CartItemMapper.class)
public interface ShoppingCartMapper {
    @Mapping(target = "userId", source = "user.id")
    ShoppingCartResponseDto toDto(ShoppingCart shoppingCart);
}
