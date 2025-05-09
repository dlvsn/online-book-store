package org.example.onlinebookstore.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.util.Set;
import lombok.Data;
import lombok.experimental.Accessors;
import org.example.onlinebookstore.validator.book.Isbn;

@Data
@Accessors(chain = true)
public class CreateBookRequestDto {
    @NotBlank(message = "Add the title of book")
    private String title;

    @NotBlank(message = "Add the author of the book ")
    private String author;

    @Isbn(message = "ISBN is invalid")
    private String isbn;

    @NotNull(message = "price can't be null")
    @Positive(message = "price can't be less than 1")
    private BigDecimal price;
    @Size(min = 10,
            max = 200,
            message = "Description must contain 10 symbols")
    private String description;
    private String coverImage;
    @NotEmpty(message = "Please, add categories for book")
    private Set<Long> categoryIds;
}
