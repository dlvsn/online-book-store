package org.example.onlinebookstore.mapper;

import org.example.onlinebookstore.config.MapperConfig;
import org.example.onlinebookstore.dto.order.OrderItemsResponseDto;
import org.example.onlinebookstore.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemsResponseDto toDto(OrderItem orderItem);
}
