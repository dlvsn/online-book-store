package org.example.onlybooks.service.shoppingcart;

import org.example.onlybooks.dto.cartitem.CartItemRequestDto;
import org.example.onlybooks.dto.cartitem.UpdateCartItemRequestDto;
import org.example.onlybooks.dto.shoppingcart.ShoppingCartResponseDto;
import org.example.onlybooks.model.User;

public interface ShoppingCartService {
    void registerNewShoppingCart(User user);

    ShoppingCartResponseDto addBookToShoppingCart(User user, CartItemRequestDto cartItem);

    ShoppingCartResponseDto getShoppingCart(User user);

    ShoppingCartResponseDto updateShoppingCart(Long itemId,
                                               User user,
                                               UpdateCartItemRequestDto cartItemDto);

    ShoppingCartResponseDto removeItemFromShoppingCart(User user, Long itemId);
}
