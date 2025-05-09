package org.example.onlinebookstore.dto.order;

public record OrderItemsResponseDto(Long id,
                                    Long bookId,
                                    int quantity) {
}
