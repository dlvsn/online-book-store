package org.example.onlinebookstore.dto.order;

import jakarta.validation.constraints.NotBlank;

public record PlaceOrderRequestDto(@NotBlank(message = "Please, add shipping address")
                                   String shippingAddress) {
}
