package org.example.onlybooks.service.order;

import java.util.List;
import org.example.onlybooks.dto.order.OrderItemsResponseDto;
import org.example.onlybooks.dto.order.OrderResponseDto;
import org.example.onlybooks.dto.order.PlaceOrderRequestDto;
import org.example.onlybooks.dto.order.UpdateOrderStatusRequestDto;
import org.example.onlybooks.model.User;

public interface OrderService {
    OrderResponseDto placeOrder(User user, PlaceOrderRequestDto placeOrderRequestDto);

    List<OrderResponseDto> getAllOrders(User user);

    OrderResponseDto updateOrderStatus(Long id, UpdateOrderStatusRequestDto requestDto);

    OrderItemsResponseDto findOrderItemByIdAndOrderId(Long orderId, Long orderItemId, User user);

    OrderResponseDto findOrderById(Long id, User user);
}
