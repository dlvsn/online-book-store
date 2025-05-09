package org.example.onlinebookstore.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Set;
import org.example.onlinebookstore.dto.cartitem.CartItemRequestDto;
import org.example.onlinebookstore.dto.cartitem.CartItemResponseDto;
import org.example.onlinebookstore.dto.cartitem.UpdateCartItemRequestDto;
import org.example.onlinebookstore.dto.shoppingcart.ShoppingCartResponseDto;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql(scripts = {
        "classpath:database/test/users/insert-users.sql",
        "classpath:database/test/shoppingcarts/insert-shoppingcart.sql",
        "classpath:database/test/books/insert-books.sql",
        "classpath:database/test/categories/insert-categories.sql",
        "classpath:database/test/books/insert-books-categories.sql",
        "classpath:database/test/cartitems/insert-cartitems.sql"
}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/test/cartitems/delete-cartitems.sql",
        "classpath:database/test/books/delete-books-categories.sql",
        "classpath:database/test/books/delete-books.sql",
        "classpath:database/test/categories/delete-categories.sql",
        "classpath:database/test/shoppingcarts/delete-shoppingcart.sql",
        "classpath:database/test/users/delete-users.sql"
}, executionPhase = AFTER_TEST_METHOD)
public class ShoppingCartControllerTest {
    private static final String USER_DETAILS = "testmail44442@mail.com";
    private static final String CART_ITEM_ENDPOINT = "/cart/items/{cartItemId}";
    private static final String SHOPPING_CART_ENDPOINT = "/cart";
    private static final Long TEST_ID = 2L;

    private static MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeAll
    static void beforeAll(@Autowired WebApplicationContext applicationContext) {
        mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
                .apply(springSecurity())
                .build();
    }

    @Test
    @DisplayName("""
            Should return the shopping cart of 
            the authenticated user when valid request is made.
            """)
    @WithUserDetails(USER_DETAILS)
    void getShoppingCart_AsUser_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                get(SHOPPING_CART_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andReturn();

        CartItemResponseDto expectedCartItems = initCartItemResponseDto();

        ShoppingCartResponseDto expected = initShoppingCartResponseDto(Set.of(expectedCartItems));

        String jsonResponse = result.getResponse().getContentAsString();

        ShoppingCartResponseDto actual = objectMapper.readValue(
                jsonResponse, ShoppingCartResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Should return BadRequest when 
            adding a cart item with an invalid quantity.
            """)
    @WithUserDetails(USER_DETAILS)
    void addCartItemWithInvalidQuantity_AsUser_BadRequest() throws Exception {
        CartItemRequestDto invalidRequest = new CartItemRequestDto(TEST_ID, -5);
        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);
        mockMvc.perform(
                post(SHOPPING_CART_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("""
            Should successfully update the 
            quantity of an existing cart item in the shopping cart.
            """)
    @WithUserDetails(USER_DETAILS)
    void addExistingCartItem_AsUser_Success() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto(4L, 5);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                post(SHOPPING_CART_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andReturn();

        CartItemResponseDto expectedCartItem = initCartItemResponseDto().setQuantity(8);
        ShoppingCartResponseDto expected = initShoppingCartResponseDto(Set.of(expectedCartItem));

        String jsonResponse = result.getResponse().getContentAsString();

        ShoppingCartResponseDto actual = objectMapper.readValue(
                jsonResponse, ShoppingCartResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Should successfully add a new cart item to the shopping cart.
            """)
    @WithUserDetails(USER_DETAILS)
    void addNonExistingCartItem_asUser_Success() throws Exception {
        CartItemRequestDto requestDto = new CartItemRequestDto(1L, 3);

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                post(SHOPPING_CART_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andReturn();

        CartItemResponseDto newCartItem = new CartItemResponseDto()
                .setCartItemId(5L)
                .setBookId(requestDto.bookId())
                .setBookTitle("Test book 1")
                .setQuantity(requestDto.quantity());

        Set<CartItemResponseDto> expectedCartItems = Set.of(initCartItemResponseDto(), newCartItem);
        ShoppingCartResponseDto expected = initShoppingCartResponseDto(expectedCartItems);

        String jsonResponse = result.getResponse().getContentAsString();

        ShoppingCartResponseDto actual = objectMapper.readValue(
                jsonResponse, ShoppingCartResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Should return NotFound when trying to add a cart item with a non-existing book ID.
            """)
    @WithUserDetails(USER_DETAILS)
    void addCartItemWithNonExistingBookId_asUser_NotFound() throws Exception {
        CartItemRequestDto invalidRequest = new CartItemRequestDto(99L, 2);
        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(
                post(SHOPPING_CART_ENDPOINT).content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("""
            Should successfully update the quantity of an existing cart item by its ID.
            """)
    @WithUserDetails(USER_DETAILS)
    void updateCartItemWithExistingCartItemId_asUser_Success() throws Exception {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(8);

        CartItemResponseDto expectedCartItem = initCartItemResponseDto()
                .setQuantity(requestDto.quantity());

        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        MvcResult result = mockMvc.perform(
                put(CART_ITEM_ENDPOINT, TEST_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk())
                .andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        ShoppingCartResponseDto expected = initShoppingCartResponseDto(Set.of(expectedCartItem));

        ShoppingCartResponseDto actual = objectMapper.readValue(
                jsonResponse, ShoppingCartResponseDto.class);

        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("""
            Should return BadRequest when trying to update a cart item with invalid data.
            """)
    @WithUserDetails(USER_DETAILS)
    void updateCartItemWithInvalidRequest_asUser_BadRequest() throws Exception {
        UpdateCartItemRequestDto invalidRequest = new UpdateCartItemRequestDto(-5);
        String jsonRequest = objectMapper.writeValueAsString(invalidRequest);
        mockMvc.perform(
                put(CART_ITEM_ENDPOINT, TEST_ID)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("""
            Should return NotFound when trying to update a cart item with a non-existing ID.
            """)
    @WithUserDetails(USER_DETAILS)
    void updateCartItemWithNonExistingId_asUser_NotFound() throws Exception {
        UpdateCartItemRequestDto requestDto = new UpdateCartItemRequestDto(5);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(
                put(CART_ITEM_ENDPOINT, 99L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("""
            Should successfully delete an existing cart item from the shopping cart
            """)
    @WithUserDetails(USER_DETAILS)
    void deleteCartItemWithExistingId_AsUser_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                delete(CART_ITEM_ENDPOINT, TEST_ID)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNoContent()).andReturn();

        ShoppingCartResponseDto expected = initShoppingCartResponseDto(Collections.emptySet());

        String jsonResponse = result.getResponse().getContentAsString();

        ShoppingCartResponseDto actual = objectMapper.readValue(
                jsonResponse, ShoppingCartResponseDto.class);

        assertEquals(expected, actual);
    }

    private ShoppingCartResponseDto initShoppingCartResponseDto(
            Set<CartItemResponseDto> cartItems) {
        return new ShoppingCartResponseDto(7L, cartItems);
    }

    private CartItemResponseDto initCartItemResponseDto() {
        return new CartItemResponseDto()
                .setCartItemId(TEST_ID)
                .setBookId(4L)
                .setBookTitle("Test book 4")
                .setQuantity(3);
    }
}
