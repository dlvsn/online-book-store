package org.example.onlinebookstore.service.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.example.onlinebookstore.dto.order.OrderItemsResponseDto;
import org.example.onlinebookstore.dto.order.OrderResponseDto;
import org.example.onlinebookstore.dto.order.PlaceOrderRequestDto;
import org.example.onlinebookstore.dto.order.UpdateOrderStatusRequestDto;
import org.example.onlinebookstore.exception.DataProcessingException;
import org.example.onlinebookstore.exception.EntityNotFoundException;
import org.example.onlinebookstore.mapper.OrderItemMapper;
import org.example.onlinebookstore.mapper.OrderMapper;
import org.example.onlinebookstore.model.Book;
import org.example.onlinebookstore.model.CartItem;
import org.example.onlinebookstore.model.Order;
import org.example.onlinebookstore.model.OrderItem;
import org.example.onlinebookstore.model.ShoppingCart;
import org.example.onlinebookstore.model.User;
import org.example.onlinebookstore.repository.order.OrderItemRepository;
import org.example.onlinebookstore.repository.order.OrderRepository;
import org.example.onlinebookstore.repository.shoppingcart.ShoppingCartRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class OrderServiceImplTest {
    @InjectMocks
    private OrderServiceImpl orderService;

    @Mock
    private OrderMapper orderMapper;

    @Mock
    private OrderItemMapper orderItemMapper;

    @Mock
    private ShoppingCartRepository shoppingCartRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @Mock
    private OrderRepository orderRepository;

    @Test
    @DisplayName("""
            Tests placing an order successfully when the shopping cart contains items.
            """)
    void placeOrder_withCartItems_Success() {
        ShoppingCart shoppingCart = initShoppingCart();
        CartItem cartItem = initCartItem();
        cartItem.setShoppingCart(shoppingCart);

        Set<CartItem> cartItems = new HashSet<>();
        cartItems.add(cartItem);
        shoppingCart.setCartItems(cartItems);

        User user = initUser();

        when(shoppingCartRepository.findByUserIdFetchCartItemsAndBooks(user.getId()))
                .thenReturn(Optional.of(shoppingCart));

        PlaceOrderRequestDto requestDto = new PlaceOrderRequestDto("test");

        when(orderMapper.toDto(any(Order.class))).thenAnswer(invocation -> {
            Order savedOrder = invocation.getArgument(0);
            return initOrderResponseDto(savedOrder);
        });

        OrderResponseDto actual = orderService.placeOrder(user, requestDto);

        assertThat(shoppingCart.getCartItems()).isEmpty();
        assertThat(actual.userId()).isEqualTo(user.getId());
        assertThat(actual.orderItems()).hasSize(1);

        verify(orderRepository).save(any(Order.class));
        verify(shoppingCartRepository).save(shoppingCart);
        verify(orderMapper).toDto(any(Order.class));
    }

    @Test
    @DisplayName("""
            Tests that placing an order with an empty 
            shopping cart throws a DataProcessingException
            """)
    void placeOrder_withEmptyCartItems_ThrowException() {
        User user = initUser();
        ShoppingCart shoppingCart = initShoppingCart();
        shoppingCart.getCartItems().clear();
        when(shoppingCartRepository.findByUserIdFetchCartItemsAndBooks(user.getId()))
                .thenReturn(Optional.of(shoppingCart));
        Assertions.assertThrows(DataProcessingException.class, () ->
                orderService.placeOrder(user, new PlaceOrderRequestDto("test")));
    }

    @Test
    @DisplayName("""
            Tests successfully finding an order by its ID and user ID.
            """)
    void findOrder_withExistingId_Success() {
        User user = initUser();
        Order order = initOrder(user);
        OrderResponseDto expected = initOrderResponseDto(order);

        when(orderRepository.findByIdAndUserId(order.getId(), user.getId()))
                .thenReturn(Optional.of(order));
        when(orderMapper.toDto(order)).thenReturn(expected);

        OrderResponseDto actual = orderService.findOrderById(order.getId(), user);

        assertThat(actual).isEqualTo(expected);

        verify(orderRepository).findByIdAndUserId(order.getId(), user.getId());
        verify(orderMapper).toDto(order);
    }

    @Test
    @DisplayName("""
            Tests that trying to find an order 
            with a non-existing ID throws an EntityNotFoundException
            """)
    void findOrder_withNonExistingId_ThrowException() {
        Long invalidId = 152L;
        User user = initUser();
        when(orderRepository.findByIdAndUserId(invalidId, user.getId()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                orderService.findOrderById(invalidId, user));
    }

    @Test
    @DisplayName("""
            Tests retrieving all orders for a specific user successfully.
            """)
    void getAllOrders_Success() {
        User user = initUser();

        Order firstOrder = initOrder(user);
        Order secondOrder = initOrder(user);

        OrderItem firstItem = initOrderItem();
        OrderItem secondItem = initSecondOrderItem();

        secondOrder.setId(2L);
        secondOrder.setOrderItems(Set.of(firstItem, secondItem));

        OrderResponseDto firstResponse = initOrderResponseDto(firstOrder);
        OrderResponseDto secondResponse = initOrderResponseDto(secondOrder);

        List<OrderResponseDto> expected = List.of(firstResponse, secondResponse);

        when(orderRepository.findAllByUserId(user.getId()))
                .thenReturn(List.of(firstOrder, secondOrder));
        when(orderMapper.toDto(firstOrder)).thenReturn(firstResponse);
        when(orderMapper.toDto(secondOrder)).thenReturn(secondResponse);

        List<OrderResponseDto> actual = orderService.getAllOrders(user);

        assertThat(actual).isEqualTo(expected);
    }

    @Test
    @DisplayName("""
            Tests successfully updating the status of an order with a valid status.
            """)
    void updateStatus_withValidStatus_Success() {
        User user = initUser();
        Order order = initOrder(user);

        UpdateOrderStatusRequestDto request = new UpdateOrderStatusRequestDto(Order.Status.PENDING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.of(order));
        order.setStatus(request.status());

        OrderResponseDto expected = initOrderResponseDto(order);

        when(orderRepository.save(any(Order.class))).thenReturn(order);
        when(orderMapper.toDto(order)).thenReturn(expected);

        OrderResponseDto actual = orderService.updateOrderStatus(order.getId(), request);

        assertThat(actual).isEqualTo(expected);

        verify(orderRepository).findById(order.getId());
        verify(orderRepository).save(order);
        verify(orderMapper).toDto(order);
    }

    @Test
    @DisplayName("""
            Tests that updating the status
             of a non-existing order throws an EntityNotFoundException.
            """)
    void updateStatus_withNonExistingStatus_ThrowException() {
        User user = initUser();
        Order order = initOrder(user);
        UpdateOrderStatusRequestDto request = new UpdateOrderStatusRequestDto(Order.Status.PENDING);
        when(orderRepository.findById(order.getId())).thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                orderService.updateOrderStatus(order.getId(), request));
    }

    @Test
    @DisplayName("""
            Tests successfully finding an order 
            item by its ID and the associated order ID for a specific user.
            """)
    void findOrderItem_withExistingIdAndOrderId_Success() {
        User user = initUser();
        Order order = initOrder(user);

        OrderItem firstItem = initOrderItem();
        OrderItem secondItem = initSecondOrderItem();
        order.setOrderItems(Set.of(firstItem, secondItem));

        when(orderItemRepository.findByIdAndOrderId(order.getId(),
                secondItem.getId(), user.getId()))
                .thenReturn(Optional.of(secondItem));

        OrderItemsResponseDto expected = new OrderItemsResponseDto(secondItem.getId(),
                secondItem.getBook().getId(), secondItem.getQuantity());

        when(orderItemMapper.toDto(secondItem)).thenReturn(expected);

        OrderItemsResponseDto actual = orderService
                .findOrderItemByIdAndOrderId(order.getId(), secondItem.getId(), user);

        assertThat(actual).isEqualTo(expected);

        verify(orderItemRepository, times(1))
                .findByIdAndOrderId(order.getId(), secondItem.getId(), user.getId());
        verify(orderItemMapper).toDto(secondItem);
    }

    @Test
    @DisplayName("""
            Tests that trying to find an order item 
            with a non-existing ID and order ID throws an EntityNotFoundException
            """)
    void findOrderItem_withNonExistingIdAndOrderId_ThrowException() {
        Long invalidId = 152L;
        User user = initUser();
        Order order = initOrder(user);
        when(orderItemRepository.findByIdAndOrderId(order.getId(), invalidId, user.getId()))
                .thenReturn(Optional.empty());
        Assertions.assertThrows(EntityNotFoundException.class, () ->
                orderService.findOrderItemByIdAndOrderId(order.getId(), invalidId, user));
    }

    private OrderResponseDto initOrderResponseDto(Order order) {
        return new OrderResponseDto(
                order.getId(),
                order.getUser().getId(),
                order.getOrderItems().stream()
                        .map(e -> new OrderItemsResponseDto(
                                e.getId(),
                                e.getBook().getId(),
                                e.getQuantity()))
                        .collect(Collectors.toSet()),
                order.getOrderDate(),
                order.getTotalPrice(),
                order.getStatus()
        );
    }

    private Order initOrder(User user) {
        Order order = new Order();
        order.setId(1L);
        order.setUser(user);
        order.setOrderItems(Set.of(initOrderItem()));
        order.setOrderDate(LocalDateTime.now());
        order.setTotalPrice(BigDecimal.valueOf(100.50));
        return order;
    }

    private OrderItem initOrderItem() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setBook(initBook());
        orderItem.setQuantity(1);
        orderItem.setPrice(BigDecimal.valueOf(45.98));
        return orderItem;
    }

    private OrderItem initSecondOrderItem() {
        OrderItem orderItem = new OrderItem();
        orderItem.setId(2L);
        orderItem.setBook(initBook());
        orderItem.setQuantity(10);
        orderItem.setPrice(BigDecimal.valueOf(45.98)
                .multiply(BigDecimal.valueOf(orderItem.getQuantity())));
        return orderItem;
    }

    private CartItem initCartItem() {
        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setBook(initBook());
        cartItem.setQuantity(1);
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
}
