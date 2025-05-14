package org.example.onlybooks.dto.order;

public record OrderItemsResponseDto(Long id,
                                    Long bookId,
                                    int quantity) {
}
