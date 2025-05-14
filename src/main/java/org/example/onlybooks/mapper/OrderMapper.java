package org.example.onlybooks.mapper;

import org.example.onlybooks.config.MapperConfig;
import org.example.onlybooks.dto.order.OrderResponseDto;
import org.example.onlybooks.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(config = MapperConfig.class,
        uses = OrderItemMapper.class)
public interface OrderMapper {
    @Mapping(target = "userId", source = "user.id")
    OrderResponseDto toDto(Order order);
}
