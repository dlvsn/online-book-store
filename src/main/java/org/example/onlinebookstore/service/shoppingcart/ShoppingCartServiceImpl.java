package org.example.onlinebookstore.service.shoppingcart;

import lombok.RequiredArgsConstructor;
import org.example.onlinebookstore.dto.cartitem.CartItemRequestDto;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    private final CartItemRepository cartItemRepository;
    private final BookRepository bookRepository;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ShoppingCartMapper shoppingCartMapper;

    @Override
    public void registerNewShoppingCart(User user) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setUser(user);
        shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public ShoppingCartResponseDto getShoppingCart(User user) {
        return shoppingCartMapper.toDto(findShoppingCart(user.getId()));
    }

    @Transactional
    @Override
    public ShoppingCartResponseDto addBookToShoppingCart(User user, CartItemRequestDto cartItem) {
        Book bookFromDb = bookRepository.findById(cartItem.bookId()).orElseThrow(() ->
                new EntityNotFoundException("Can't find book by id " + cartItem.bookId()));
        ShoppingCart shoppingCart = findShoppingCart(user.getId());
        shoppingCart.getCartItems().stream()
                .filter(item -> item.getBook().getId().equals(cartItem.bookId()))
                .findFirst()
                .ifPresentOrElse(item ->
                                item.setQuantity(item.getQuantity() + cartItem.quantity()),
                        () ->
                                addCartItem(shoppingCart, bookFromDb, cartItem.quantity()));
        shoppingCartRepository.save(shoppingCart);
        return shoppingCartMapper.toDto(shoppingCart);
    }

    @Transactional
    @Override
    public ShoppingCartResponseDto updateShoppingCart(Long itemId,
                                                      User user,
                                                      UpdateCartItemRequestDto cartItemDto) {
        CartItem cartItem = cartItemRepository.findByIdAndShoppingCartId(itemId, user.getId())
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find item by id " + itemId));
        cartItem.setQuantity(cartItemDto.quantity());
        cartItemRepository.save(cartItem);
        return shoppingCartMapper.toDto(findShoppingCart(user.getId()));
    }

    @Transactional
    @Override
    public ShoppingCartResponseDto removeItemFromShoppingCart(User user, Long itemId) {
        cartItemRepository.deleteCartItemByIdAndShoppingCartId(itemId, user.getId());
        return shoppingCartMapper.toDto(findShoppingCart(user.getId()));
    }

    private void addCartItem(ShoppingCart shoppingCart, Book book, int quantity) {
        CartItem cartItem = new CartItem();
        cartItem.setShoppingCart(shoppingCart);
        cartItem.setBook(book);
        cartItem.setQuantity(quantity);
        shoppingCart.getCartItems().add(cartItem);
    }

    private ShoppingCart findShoppingCart(Long id) {
        return shoppingCartRepository.findByUserIdFetchCartItemsAndBooks(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Can't find shopping cart by id " + id));
    }
}
