package org.example.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.example.onlinebookstore.dto.cartitem.CartItemRequestDto;
import org.example.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import org.example.onlinebookstore.dto.shoppingcart.ShoppingCartResponseDto;
import org.example.onlinebookstore.model.User;
import org.example.onlinebookstore.service.shoppingcart.ShoppingCartService;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Shopping Cart", description = "Controller for managing the user's shopping cart.")
@RequiredArgsConstructor
@RestController
@RequestMapping("/cart")
public class ShoppingCartController {
    private final ShoppingCartService shoppingCartService;

    @Operation(summary = "Get the user's shopping cart",
            description = "This method allows"
                    + " retrieving the shopping cart of an authenticated user.")
    @GetMapping
    public ShoppingCartResponseDto getShoppingCart(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.getShoppingCart(user);
    }

    @Operation(summary = "Add a book to the shopping cart",
            description = "This method allows adding a book to the user's shopping cart. "
                    + "The user must be authenticated.")
    @PostMapping
    public ShoppingCartResponseDto addBookToShoppingCart(Authentication authentication,
                                                         @RequestBody
                                                         @Valid
                                                         CartItemRequestDto cartItem) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.addBookToShoppingCart(user, cartItem);
    }

    @Operation(summary = "Update an item in the shopping cart",
            description = "This method allows updating the "
                    + "details of an item in the user's shopping cart.")
    @PutMapping("/items/{cartItemId}")
    public ShoppingCartResponseDto updateCartItem(Authentication authentication,
                                                  @PathVariable
                                                  @Positive
                                                  Long cartItemId,
                                                  @RequestBody
                                                  @Valid
                                                  UpdateCartItemRequestDto cartItem) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.updateShoppingCart(cartItemId, user, cartItem);
    }

    @Operation(summary = "Remove an item from the shopping cart",
            description = "This method allows removing an item from the user's shopping cart.")
    @DeleteMapping("/items/{cartItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ShoppingCartResponseDto deleteCartItem(Authentication authentication,
                                                  @PathVariable
                                                  @Positive
                                                  Long cartItemId) {
        User user = (User) authentication.getPrincipal();
        return shoppingCartService.removeItemFromShoppingCart(user, cartItemId);
    }
}
