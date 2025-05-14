package org.example.onlybooks.dto.cartitem;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@EqualsAndHashCode
@Accessors(chain = true)
public class CartItemResponseDto {
    private Long cartItemId;
    private Long bookId;
    private String bookTitle;
    private int quantity;
}
