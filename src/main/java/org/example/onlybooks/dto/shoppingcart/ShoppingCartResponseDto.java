package org.example.onlybooks.dto.shoppingcart;

import java.util.Set;
import org.example.onlybooks.dto.cartitem.CartItemResponseDto;

public record ShoppingCartResponseDto(Long userId,
                                      Set<CartItemResponseDto> cartItems){
}
