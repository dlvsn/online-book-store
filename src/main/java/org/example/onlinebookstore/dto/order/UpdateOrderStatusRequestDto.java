package org.example.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotNull;
import org.example.onlinebookstore.model.Order;

public record UpdateOrderStatusRequestDto(@NotNull(message = "Please, set order status")
                                          Order.Status status) {
}
