package org.example.onlinebookstore.service.shoppingcart;

import org.example.onlinebookstore.dto.cartitem.CartItemRequestDto;
import org.example.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import org.example.onlinebookstore.dto.shoppingcart.ShoppingCartResponseDto;
import org.example.onlinebookstore.model.User;

public interface ShoppingCartService {
    void registerNewShoppingCart(User user);

    ShoppingCartResponseDto addBookToShoppingCart(User user, CartItemRequestDto cartItem);

    ShoppingCartResponseDto getShoppingCart(User user);

    ShoppingCartResponseDto updateShoppingCart(Long itemId,
                                               User user,
                                               UpdateCartItemRequestDto cartItemDto);

    ShoppingCartResponseDto removeItemFromShoppingCart(User user, Long itemId);
}
