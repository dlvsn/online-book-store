package org.example.onlybooks.dto.book;

import java.math.BigDecimal;

public record BookWithoutCategoryIdsResponseDto(String title,
                                                String author,
                                                String isbn,
                                                BigDecimal price,
                                                String description,
                                                String coverImage) {
}
