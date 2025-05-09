package org.example.onlinebookstore.mapper;

import org.example.onlinebookstore.config.MapperConfig;
import org.example.onlinebookstore.dto.cartitem.CartItemRequestDto;
import org.example.onlinebookstore.dto.cartitem.CartItemResponseDto;
import org.example.onlinebookstore.model.CartItem;
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
