package org.example.onlybooks.mapper;

import org.example.onlybooks.config.MapperConfig;
import org.example.onlybooks.dto.cartitem.CartItemRequestDto;
import org.example.onlybooks.dto.cartitem.CartItemResponseDto;
import org.example.onlybooks.model.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface CartItemMapper {
    @Mapping(target = "cartItemId", source = "id")
    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    CartItemResponseDto toDto(CartItem cartItem);

    CartItem toEntity(CartItemRequestDto cartItem);
}
