package org.example.onlinebookstore.dto.order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import org.example.onlinebookstore.model.Order;

public record OrderResponseDto(Long id,
                               Long userId,
                               Set<OrderItemsResponseDto> orderItems,
                               LocalDateTime orderDate,
                               BigDecimal totalPrice,
                               Order.Status status) {
}
