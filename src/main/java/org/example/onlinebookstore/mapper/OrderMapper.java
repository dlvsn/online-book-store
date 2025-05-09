package org.example.onlinebookstore.mapper;

import org.example.onlinebookstore.config.MapperConfig;
import org.example.onlinebookstore.dto.order.OrderResponseDto;
import org.example.onlinebookstore.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class,
        uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderResponseDto toDto(Order order);
}
