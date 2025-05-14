package org.example.onlybooks.service.order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.example.onlybooks.dto.order.OrderItemsResponseDto;
import org.example.onlybooks.dto.order.OrderResponseDto;
import org.example.onlybooks.dto.order.PlaceOrderRequestDto;
import org.example.onlybooks.dto.order.UpdateOrderStatusRequestDto;
import org.example.onlybooks.exception.DataProcessingException;
import org.example.onlybooks.exception.EntityNotFoundException;
import org.example.onlybooks.mapper.OrderItemMapper;
import org.example.onlybooks.mapper.OrderMapper;
import org.example.onlybooks.model.CartItem;
import org.example.onlybooks.model.Order;
import org.example.onlybooks.model.OrderItem;
import org.example.onlybooks.model.ShoppingCart;
import org.example.onlybooks.model.User;
import org.example.onlybooks.repository.order.OrderItemRepository;
import org.example.onlybooks.repository.order.OrderRepository;
import org.example.onlybooks.repository.shoppingcart.ShoppingCartRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class OrderServiceImpl implements OrderService {
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    @Transactional
    @Override
    public OrderResponseDto placeOrder(User user, PlaceOrderRequestDto placeOrderRequestDto) {
        ShoppingCart shoppingCart = shoppingCartRepository
                .findByUserIdFetchCartItemsAndBooks(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find shopping cart by id "
                        + user.getId()));
        Set<CartItem> cartItems = shoppingCart.getCartItems();

        if (cartItems.isEmpty()) {
            throw new DataProcessingException("Can't place order. Cart with id "
                    + user.getId()
                    + " is empty");
        }

        String shippingAddress = getShippingAddress(placeOrderRequestDto, user);
        Order order = createNewOrder(user, cartItems);
        order.setShippingAddress(shippingAddress);
        orderRepository.save(order);
        cartItems.clear();
        shoppingCartRepository.save(shoppingCart);
        return orderMapper.toDto(order);
    }

    @Override
    public List<OrderResponseDto> getAllOrders(User user) {
        return orderRepository.findAllByUserId(user.getId()).stream()
                .map(orderMapper::toDto)
                .toList();
    }

    @Override
    public OrderResponseDto updateOrderStatus(Long id, UpdateOrderStatusRequestDto requestDto) {
        Order order = orderRepository.findById(id).orElseThrow(()
                -> new EntityNotFoundException("Can't find order by id " + id));
        order.setStatus(requestDto.status());
        orderRepository.save(order);
        return orderMapper.toDto(order);
    }

    @Override
    public OrderItemsResponseDto findOrderItemByIdAndOrderId(Long orderId,
                                                             Long orderItemId,
                                                             User user) {
        OrderItem orderItem = orderItemRepository
                .findByIdAndOrderId(orderId, orderItemId, user.getId())
                .orElseThrow(() -> new EntityNotFoundException("Can't find order with id "
                        + orderId + " and order item id "
                        + orderItemId));
        return orderItemMapper.toDto(orderItem);
    }

    @Override
    public OrderResponseDto findOrderById(Long id, User user) {
        Order order = orderRepository.findByIdAndUserId(id, user.getId()).orElseThrow(
                () -> new EntityNotFoundException("Can't find order by id: " + id));
        return orderMapper.toDto(order);
    }

    private BigDecimal calculateOrderTotalPrice(Set<OrderItem> orderItems) {
        return orderItems.stream()
                .map(items -> items.getPrice().multiply(BigDecimal.valueOf(items.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Set<OrderItem> mapCartItemsToOrderItems(Set<CartItem> cartItems, Order order) {
        return cartItems.stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setBook(cartItem.getBook());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPrice(cartItem.getBook().getPrice());
                    return orderItem;
                }).collect(Collectors.toSet());
    }

    private Order createNewOrder(User user,
                                 Set<CartItem> cartItems) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderItems(mapCartItemsToOrderItems(cartItems, order));
        order.setTotalPrice(calculateOrderTotalPrice(order.getOrderItems()));
        return order;
    }

    private String getShippingAddress(PlaceOrderRequestDto requestDto, User user) {
        return requestDto.shippingAddress() != null && !requestDto.shippingAddress().isBlank()
                ? requestDto.shippingAddress() : user.getShippingAddress();
    }
}
