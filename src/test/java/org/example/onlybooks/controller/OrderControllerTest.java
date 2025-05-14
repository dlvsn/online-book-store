package org.example.onlybooks.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.AFTER_TEST_METHOD;
import static org.springframework.test.context.jdbc.Sql.ExecutionPhase.BEFORE_TEST_METHOD;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.example.onlybooks.dto.order.OrderItemsResponseDto;
import org.example.onlybooks.dto.order.OrderResponseDto;
import org.example.onlybooks.dto.order.PlaceOrderRequestDto;
import org.example.onlybooks.dto.order.UpdateOrderStatusRequestDto;
import org.example.onlybooks.model.Order;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
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
        "classpath:database/test/cartitems/insert-cartitems.sql",
        "classpath:database/test/orders/insert-orders.sql",
        "classpath:database/test/orders/insert-order-items.sql"
}, executionPhase = BEFORE_TEST_METHOD)
@Sql(scripts = {
        "classpath:database/test/orders/delete-order-items.sql",
        "classpath:database/test/orders/delete-orders.sql",
        "classpath:database/test/cartitems/delete-cartitems.sql",
        "classpath:database/test/books/delete-books-categories.sql",
        "classpath:database/test/books/delete-books.sql",
        "classpath:database/test/categories/delete-categories.sql",
        "classpath:database/test/shoppingcarts/delete-shoppingcart.sql",
        "classpath:database/test/users/delete-users.sql"
}, executionPhase = AFTER_TEST_METHOD)
public class OrderControllerTest {
    private static final String USER_DETAILS = "testmail44442@mail.com";
    private static final String USER_WITH_EMPTY_SHOPPING_CART = "testmail12345@mail.com";
    private static final String ORDER_ID_ITEM_ID_ENDPOINT = "/orders/{orderId}/items/{itemId}";
    private static final String ORDER_ENDPOINT = "/orders";
    private static final String ORDER_ID_ENDPOINT = "/orders/{id}";
    private static final String ORDER_ID_ITEMS_ENDPOINT = "/orders/{orderId}/items";

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
            Tests the successful placing of an order 
            with a valid address by an authenticated user. 
            The response should return the created order with the associated items.
            """)
    @WithUserDetails(USER_DETAILS)
    void placeOrderWithValidAddress_asUser_Success() throws Exception {
        PlaceOrderRequestDto placeOrderRequestDto = initAddressRequest();

        String jsonRequest = objectMapper.writeValueAsString(placeOrderRequestDto);

        MvcResult result = mockMvc.perform(
                post(ORDER_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isCreated()).andReturn();

        OrderItemsResponseDto orderItemsResponseDto = initOrderItemResponse();

        OrderResponseDto expected = initOrderResponseDto(Set.of(orderItemsResponseDto));

        String jsonResponse = result.getResponse().getContentAsString();

        OrderResponseDto actual = objectMapper.readValue(jsonResponse, OrderResponseDto.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(
                        LocalDateTime.class,
                        BigDecimal.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Tests the scenario where the user tries to place an order with an empty shopping cart. 
            The server is expected to return a 500 (Internal Server Error).
            """)
    @WithUserDetails(USER_WITH_EMPTY_SHOPPING_CART)
    void placeOrderWithEmptyShoppingCart_asUser_InternalServerError() throws Exception {
        PlaceOrderRequestDto placeOrderRequestDto = initAddressRequest();
        String jsonRequest = objectMapper.writeValueAsString(placeOrderRequestDto);
        mockMvc.perform(
                post(ORDER_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("""
            Tests placing an order with an empty address. 
            The server is expected to return a 400 (Bad Request) error 
            due to the invalid address.
            """)
    @WithUserDetails(USER_DETAILS)
    void placeOrderWithEmptyAddress_asUser_BadRequest() throws Exception {
        PlaceOrderRequestDto placeOrderRequestDto = new PlaceOrderRequestDto(" ");
        String jsonRequest = objectMapper.writeValueAsString(placeOrderRequestDto);
        mockMvc.perform(
                post(ORDER_ENDPOINT)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("""
            Tests the successful retrieval of all orders for an authenticated user. 
            The response should return the list of orders with the correct data.
            """)
    @WithUserDetails(USER_DETAILS)
    void getAllOrders_asUser_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                get(ORDER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        List<OrderResponseDto> expected = List.of(initExistingOrderInTestDb());
        List<OrderResponseDto> actual = objectMapper.readValue(
                jsonResponse, new TypeReference<>() {});

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(
                        LocalDateTime.class,
                        BigDecimal.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Tests the retrieval of an order by its existing ID.
             The server should return the details of the specified order.
            """)
    @WithUserDetails(USER_DETAILS)
    void findOrderWithExistingId_asUser_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                get(ORDER_ID_ITEMS_ENDPOINT, 2L)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        OrderResponseDto expected = initExistingOrderInTestDb();

        OrderResponseDto actual = objectMapper.readValue(jsonResponse, OrderResponseDto.class);

        assertThat(actual).usingRecursiveComparison()
                .ignoringFieldsOfTypes(
                        LocalDateTime.class,
                        BigDecimal.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Tests the retrieval of an order by a non-existing ID.
             The server should return a 404 (Not Found) error.
            """)
    @WithUserDetails(USER_DETAILS)
    void findOrderWithNonExistingId_asUser_NotFound() throws Exception {
        mockMvc.perform(
                get(ORDER_ID_ITEMS_ENDPOINT, 3L)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("""
            Tests the successful retrieval of an order item by its ID and order ID. 
            The response should return the correct order item details.
            """)
    @WithUserDetails(USER_DETAILS)
    void findOrderItemByIdAndOrderId_asUser_Success() throws Exception {
        MvcResult result = mockMvc.perform(
                get(ORDER_ID_ITEM_ID_ENDPOINT,
                        2L,
                        2L)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        OrderItemsResponseDto expected = initExistingOrderItemInTestDb();

        OrderItemsResponseDto actual = objectMapper.readValue(
                jsonResponse, OrderItemsResponseDto.class);

        assertEquals(actual, expected);
    }

    @Test
    @DisplayName("""
            Tests the retrieval of an order item with non-existing IDs. 
            The server should return a 404 (Not Found) error.
            """)
    @WithUserDetails(USER_DETAILS)
    void findOrderItemNonExistingIds_asUser_NotFound() throws Exception {
        mockMvc.perform(
                get(ORDER_ID_ITEM_ID_ENDPOINT, 3L, 4L)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("""
            Tests the successful update of an order by an admin. 
            The admin is expected to change the order status, and 
            the response should return the updated order details
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateOrder_asAdmin_Success() throws Exception {
        UpdateOrderStatusRequestDto statusRequest =
                new UpdateOrderStatusRequestDto(Order.Status.CANCELED);
        String jsonRequest = objectMapper.writeValueAsString(statusRequest);

        MvcResult result = mockMvc.perform(
                patch(ORDER_ID_ENDPOINT, 2L)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isOk()).andReturn();

        String jsonResponse = result.getResponse().getContentAsString();

        OrderResponseDto expected = updatedExistingOrderInTestDb();

        OrderResponseDto actual = objectMapper.readValue(jsonResponse, OrderResponseDto.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFieldsOfTypes(
                        LocalDateTime.class,
                        BigDecimal.class)
                .isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Tests the update of an order status with a null request by an admin. 
            The server is expected to return a 400 (Bad Request) 
            error due to the invalid status value.
            """)
    @WithMockUser(username = "admin", roles = "ADMIN")
    void updateOrderStatusWithNullRequest_asAdmin_BadRequest() throws Exception {
        UpdateOrderStatusRequestDto requestDto = new UpdateOrderStatusRequestDto(null);
        String jsonRequest = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(
                patch(ORDER_ID_ENDPOINT, 2L)
                        .content(jsonRequest).contentType(MediaType.APPLICATION_JSON)
        ).andExpect(status().isBadRequest());
    }

    private PlaceOrderRequestDto initAddressRequest() {
        return new PlaceOrderRequestDto("test address, 12");
    }

    private OrderResponseDto initOrderResponseDto(Set<OrderItemsResponseDto> orderItems) {
        return new OrderResponseDto(
                4L,
                7L,
                orderItems,
                LocalDateTime.now(),
                BigDecimal.valueOf(752.97),
                Order.Status.PENDING);
    }

    private OrderItemsResponseDto initExistingOrderItemInTestDb() {
        return new OrderItemsResponseDto(
                2L,
                2L,
                5
        );
    }

    private OrderResponseDto initExistingOrderInTestDb() {
        return new OrderResponseDto(
                2L,
                7L,
                Set.of(initExistingOrderItemInTestDb()),
                LocalDateTime.now(),
                BigDecimal.valueOf(756.10),
                Order.Status.COMPLETED
        );
    }

    private OrderResponseDto updatedExistingOrderInTestDb() {
        return new OrderResponseDto(
                2L,
                7L,
                Set.of(initExistingOrderItemInTestDb()),
                LocalDateTime.now(),
                BigDecimal.valueOf(756.10),
                Order.Status.CANCELED
        );
    }

    private OrderItemsResponseDto initOrderItemResponse() {
        return new OrderItemsResponseDto(
                5L,
                4L,
                3
        );
    }
}
