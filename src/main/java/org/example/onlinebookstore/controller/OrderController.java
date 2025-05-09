package org.example.onlinebookstore.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.onlinebookstore.dto.order.OrderItemsResponseDto;
import org.example.onlinebookstore.dto.order.OrderResponseDto;
import org.example.onlinebookstore.dto.order.PlaceOrderRequestDto;
import org.example.onlinebookstore.dto.order.UpdateOrderStatusRequestDto;
import org.example.onlinebookstore.model.User;
import org.example.onlinebookstore.service.order.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Order Controller", description = "API for managing user orders, "
        + "including placing orders, retrieving orders, and updating order status.")
@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderService orderService;

    @Operation(summary = "Place a new order",
            description = "Allows an authenticated user "
                    + "to place a new order by providing order details."
    )
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponseDto placeOrder(Authentication authentication,
                           @RequestBody
                           @Valid
                           PlaceOrderRequestDto requestDto) {
        User user = (User) authentication.getPrincipal();
        return orderService.placeOrder(user, requestDto);
    }

    @Operation(summary = "Retrieve all orders",
            description = "Returns a list of all "
                    + "orders placed by the authenticated user."
    )
    @GetMapping
    public List<OrderResponseDto> getAllOrders(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        return orderService.getAllOrders(user);
    }

    @Operation(summary = "Find order by ID",
            description = "Fetches details of a specific "
                    + "order by its ID for the authenticated user."
    )
    @GetMapping("/{orderId}/items")
    public OrderResponseDto findOrderById(Authentication authentication,
                                          @PathVariable
                                          @Positive
                                          Long orderId) {
        User user = (User) authentication.getPrincipal();
        return orderService.findOrderById(orderId, user);
    }

    @Operation(summary = "Find order item by ID and order ID",
            description = "Fetches details of a specific item within an order "
                    + "by its ID and the order's ID for the authenticated user."
    )
    @GetMapping("/{orderId}/items/{itemId}")
    public OrderItemsResponseDto findOrderItemByIdAndOrderId(Authentication authentication,
                                                             @PathVariable
                                                             @Positive
                                                             Long orderId,
                                                             @Positive
                                                             @PathVariable Long itemId) {
        User user = (User) authentication.getPrincipal();
        return orderService.findOrderItemByIdAndOrderId(orderId, itemId, user);
    }

    @Operation(summary = "Update order status",
            description = "Allows an administrator to update "
                    + "the status of an order using its ID and the new status details."
    )
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PatchMapping("/{id}")
    public OrderResponseDto updateOrderStatus(@Positive
                                              @PathVariable Long id,
                                              @RequestBody
                                              @Valid
                                              UpdateOrderStatusRequestDto requestDto) {
        return orderService.updateOrderStatus(id, requestDto);
    }
}
