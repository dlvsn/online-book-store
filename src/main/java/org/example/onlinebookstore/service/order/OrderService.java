package org.example.onlinebookstore.service.order;

import java.util.List;
import org.example.onlinebookstore.dto.order.OrderItemsResponseDto;
import org.example.onlinebookstore.dto.order.OrderResponseDto;
import org.example.onlinebookstore.dto.order.PlaceOrderRequestDto;
import org.example.onlinebookstore.dto.order.UpdateOrderStatusRequestDto;
import org.example.onlinebookstore.model.User;

public interface OrderService {
    OrderResponseDto placeOrder(User user, PlaceOrderRequestDto placeOrderRequestDto);

    List<OrderResponseDto> getAllOrders(User user);

    OrderResponseDto updateOrderStatus(Long id, UpdateOrderStatusRequestDto requestDto);

    OrderItemsResponseDto findOrderItemByIdAndOrderId(Long orderId, Long orderItemId, User user);

    OrderResponseDto findOrderById(Long id, User user);
}
