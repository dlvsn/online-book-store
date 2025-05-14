package org.example.onlybooks.mapper;

import org.example.onlybooks.config.MapperConfig;
import org.example.onlybooks.dto.order.OrderItemsResponseDto;
import org.example.onlybooks.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class)
public interface OrderItemMapper {
    @Mapping(target = "bookId", source = "book.id")
    OrderItemsResponseDto toDto(OrderItem orderItem);
}
