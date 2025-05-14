package org.example.onlybooks.dto.order;

import jakarta.validation.constraints.NotNull;
import org.example.onlybooks.model.Order;

public record UpdateOrderStatusRequestDto(@NotNull(message = "Please, set order status")
                                          Order.Status status) {
}
