package org.example.onlinebookstore.service.shoppingcart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import org.example.onlinebookstore.dto.cartitem.CartItemRequestDto;
import org.example.onlinebookstore.dto.cartitem.CartItemResponseDto;
import org.example.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import org.example.onlinebookstore.dto.shoppingcart.ShoppingCartResponseDto;
import org.example.onlinebookstore.exception.EntityNotFoundException;
import org.example.onlinebookstore.mapper.ShoppingCartMapper;
import org.example.onlinebookstore.model.Book;
import org.example.onlinebookstore.model.CartItem;
import org.example.onlinebookstore.model.ShoppingCart;
import org.example.onlinebookstore.model.User;
import org.example.onlinebookstore.repository.book.BookRepository;
import org.example.onlinebookstore.repository.shoppingcart.CartItemRepository;
import org.example.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ShoppingCartServiceImplTest {

    @InjectMocks
    private ShoppingCartServiceImpl shoppingCartService;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ShoppingCartMapper shoppingCartMapper;

    @Test
    @DisplayName("""
            Retrieve a shopping cart with items 
            for a user by their ID. The cart contains valid cart items linked to the user.
            """)
    void getShoppingCart_byUserIdWithCartItems_Success() {
        User user = initUser();
        ShoppingCart shoppingCart = initShoppingCart();

        CartItem cartItem = initCartItem();
        cartItem.setShoppingCart(shoppingCart);

        shoppingCart.setCartItems(Set.of(cartItem));

        when(shoppingCartRepository
                .findByUserIdFetchCartItemsAndBooks(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        Set<CartItemResponseDto> cartItems = Set.of(initCartItemResponseDto(cartItem));

        ShoppingCartResponseDto expected = initResponseDto(shoppingCart, cartItems);

        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartResponseDto actual = shoppingCartService.getShoppingCart(user);

        assertThat(actual).isEqualTo(expected);
        verify(shoppingCartRepository, times(1)).findByUserIdFetchCartItemsAndBooks(user.getId());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("""
            Retrieve an empty shopping cart for a user 
            by their ID. The cart is linked to the user but contains no items.
            """)
    void getShoppingCart_byUserIdWithoutCartItems_Success() {
        User user = initUser();
        ShoppingCart shoppingCart = initShoppingCart();
        shoppingCart.setCartItems(Collections.emptySet());

        when(shoppingCartRepository.findByUserIdFetchCartItemsAndBooks(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        ShoppingCartResponseDto expected = initResponseDto(shoppingCart, Collections.emptySet());

        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartResponseDto actual = shoppingCartService.getShoppingCart(user);

        assertThat(actual).isEqualTo(expected);
        verify(shoppingCartRepository, times(1)).findByUserIdFetchCartItemsAndBooks(user.getId());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("""
            Add a book to the shopping cart where the item already exists. 
            The quantity of the existing item is updated successfully.
            """)
    void addCartItem_ifItemExist_Success() {
        ShoppingCart shoppingCart = initShoppingCart();

        CartItem cartItem = initCartItem();
        cartItem.setShoppingCart(shoppingCart);

        Set<CartItem> cartItems = Set.of(cartItem);
        shoppingCart.setCartItems(cartItems);

        Book book = initBook();
        User user = initUser();

        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(1L, 5);

        when(bookRepository.findById(cartItemRequestDto.bookId())).thenReturn(Optional.of(book));
        when(shoppingCartRepository.findByUserIdFetchCartItemsAndBooks(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        cartItem.setQuantity(cartItem.getQuantity() + cartItemRequestDto.quantity());

        CartItemResponseDto cartItemResponseDto = initCartItemResponseDto(cartItem);

        ShoppingCartResponseDto expected = initResponseDto(shoppingCart,
                Set.of(cartItemResponseDto));

        when(shoppingCartRepository.save(shoppingCart)).thenReturn(shoppingCart);
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartResponseDto actual = shoppingCartService
                .addBookToShoppingCart(user, cartItemRequestDto);

        assertThat(actual).isEqualTo(expected);
        verify(bookRepository, times(1)).findById(cartItemRequestDto.bookId());
        verify(shoppingCartRepository, times(1)).findByUserIdFetchCartItemsAndBooks(user.getId());
        verify(shoppingCartRepository, times(1)).save(shoppingCart);
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("""
            Attempt to add a book to the shopping cart 
            using a non-existing book ID. The operation 
            fails, and an EntityNotFoundException is thrown.
            """)
    void addCartItem_withNonExistingBookId_ThrowException() {
        CartItemRequestDto cartItemRequestDto = new CartItemRequestDto(89L, 2);

        when(bookRepository.findById(cartItemRequestDto.bookId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                shoppingCartService.addBookToShoppingCart(initUser(), cartItemRequestDto));
    }

    @Test
    @DisplayName("""
            Update the quantity of an existing cart item in the shopping cart 
            for a given user. The updated cart is returned successfully.
            """)
    void updateCartItem_Success() {
        User user = initUser();

        CartItem cartItem = initCartItem();
        ShoppingCart shoppingCart = initShoppingCart();
        cartItem.setShoppingCart(shoppingCart);
        shoppingCart.setCartItems(Set.of(cartItem));

        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(1);

        when(cartItemRepository.findByIdAndShoppingCartId(cartItem.getId(), user.getId()))
                .thenReturn(Optional.of(cartItem));

        cartItem.setQuantity(requestDto.quantity());
        CartItemResponseDto cartItemResponseDto = initCartItemResponseDto(cartItem);

        ShoppingCartResponseDto expected = initResponseDto(shoppingCart,
                Set.of(cartItemResponseDto));

        when(cartItemRepository.save(cartItem)).thenReturn(cartItem);
        when(shoppingCartRepository.findByUserIdFetchCartItemsAndBooks(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartResponseDto actual = shoppingCartService
                .updateShoppingCart(cartItem.getId(), user, requestDto);

        assertThat(actual).isEqualTo(expected);

        verify(cartItemRepository, times(1))
                .findByIdAndShoppingCartId(cartItem.getId(), user.getId());
        verify(cartItemRepository, times(1)).save(cartItem);
        verify(shoppingCartRepository, times(1))
                .findByUserIdFetchCartItemsAndBooks(user.getId());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(shoppingCartRepository);
        verifyNoMoreInteractions(cartItemRepository);
    }

    @Test
    @DisplayName("""
            Attempt to update the quantity
            of a cart item using a non-existing cart item ID for a given user. 
            The operation fails, and an EntityNotFoundException is thrown.
            """)
    void updateCartItem_withNonExistingCartItemId_ThrowException() {
        Long invalidId = 99L;
        User user = initUser();
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(1);
        when(cartItemRepository.findByIdAndShoppingCartId(invalidId, user.getId()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                shoppingCartService.updateShoppingCart(invalidId, user, requestDto));
    }

    @Test
    @DisplayName("""
            Should successfully remove an item with an existing ID
            from the shopping cart and return the updated shopping cart
            """)
    void deleteCartItem_withExistingId_Success() {
        User user = initUser();
        CartItem cartItem = initCartItem();
        ShoppingCart shoppingCart = initShoppingCart();

        doNothing().when(cartItemRepository)
                .deleteCartItemByIdAndShoppingCartId(cartItem.getId(), user.getId());
        shoppingCart.setCartItems(Collections.emptySet());

        when(shoppingCartRepository.findByUserIdFetchCartItemsAndBooks(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        ShoppingCartResponseDto expected = initResponseDto(shoppingCart, Collections.emptySet());
        when(shoppingCartMapper.toDto(shoppingCart)).thenReturn(expected);

        ShoppingCartResponseDto actual = shoppingCartService
                .removeItemFromShoppingCart(user, cartItem.getId());

        assertThat(actual).isEqualTo(expected);
        verify(cartItemRepository, times(1))
                .deleteCartItemByIdAndShoppingCartId(cartItem.getId(), user.getId());
        verify(shoppingCartRepository, times(1))
                .findByUserIdFetchCartItemsAndBooks(user.getId());
        verify(shoppingCartMapper, times(1)).toDto(shoppingCart);
        verifyNoMoreInteractions(cartItemRepository);
        verifyNoMoreInteractions(shoppingCartRepository);
    }

    @Test
    @DisplayName("""
            Should throw EntityNotFoundException when attempting
            to remove an item with a non-existing ID from the shopping cart
            """)
    void deleteCartItem_withNonExistingId_ThrowException() {
        Long invalidId = 99L;
        User user = initUser();
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                shoppingCartService.removeItemFromShoppingCart(user, invalidId));
    }

    private User initUser() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test mail");
        user.setPassword("test password");
        user.setFirstName("test name");
        user.setLastName("test last name");
        return user;
    }

    private ShoppingCart initShoppingCart() {
        ShoppingCart cart = new ShoppingCart();
        cart.setId(1L);
        cart.setUser(initUser());
        return cart;
    }

    private CartItem initCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(initBook());
        cartItem.setQuantity(2);
        return cartItem;
    }

    private Book initBook() {
        Book book = new Book();
        book.setId(1L);
        book.setTitle("test book");
        book.setAuthor("test author");
        book.setPrice(BigDecimal.valueOf(45.98));
        book.setIsbn("test");
        return book;
    }

    private ShoppingCartResponseDto initResponseDto(ShoppingCart shoppingCart,
                                                    Set<CartItemResponseDto> cartItems) {
        return new ShoppingCartResponseDto(shoppingCart.getId(), cartItems);
    }

    private CartItemResponseDto initCartItemResponseDto(CartItem cartItem) {
        return new CartItemResponseDto()
                .setCartItemId(cartItem.getId())
                .setBookId(cartItem.getBook().getId())
                .setBookTitle(cartItem.getBook().getTitle())
                .setQuantity(cartItem.getQuantity());
    }
}
