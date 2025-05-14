package org.example.onlybooks.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import org.example.onlybooks.model.Order;

public record OrderResponseDto(Long id,
                               Long userId,
                               Set<OrderItemsResponseDto> orderItems,
                               LocalDateTime orderDate,
                               BigDecimal totalPrice,
                               Order.Status status) {
}
