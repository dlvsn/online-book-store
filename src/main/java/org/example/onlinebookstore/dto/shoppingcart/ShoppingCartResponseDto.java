package org.example.onlinebookstore.dto.shoppingcart;

import java.util.Set;
import org.example.onlinebookstore.dto.cartitem.CartItemResponseDto;

public record ShoppingCartResponseDto(Long userId,
                                      Set<CartItemResponseDto> cartItems){
}
