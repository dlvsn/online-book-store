package org.example.onlybooks.dto.cartitem;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CartItemRequestDto(@NotNull
                                 @Positive(message = "book id can't be less than 1")
                                 Long bookId,
                                 @Positive(message = "quantity can't be less than 1")
                                 int quantity) {
}
