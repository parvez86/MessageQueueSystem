package com.example.MessageQueueConsumer.service;

import com.example.MessageQueueConsumer.dto.OrderDto;
import com.example.MessageQueueConsumer.entity.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface OrderService {
    /**
     * Create a new order
     * @param request Order creation request
     * @return Created order response
     */
    OrderDto.Response createOrder(OrderDto.Request request);

    /**
     * Get order by ID
     * @param orderId Order unique identifier
     * @return Order response
     */
    OrderDto.Response getOrderById(String orderId);

    /**
     * Get all orders with pagination
     * @param pageable Pagination information
     * @return Page of order responses
     */
    Page<OrderDto.Response> getAllOrders(Pageable pageable);

    /**
     * Update order information
     * @param orderId Order unique identifier
     * @param request Order update request
     * @return Updated order response
     */
    OrderDto.Response updateOrder(String orderId, OrderDto.UpdateRequest request);

    /**
     * Delete order by ID
     * @param orderId Order unique identifier
     */
    void deleteOrder(String orderId);

    /**
     * Get orders by status
     * @param status Order status
     * @return List of order responses
     */
    List<OrderDto.Response> getOrdersByStatus(OrderStatus status);

    /**
     * Get orders by customer ID
     * @param customerId Customer unique identifier
     * @return List of order responses
     */
    List<OrderDto.Response> getOrdersByCustomerId(String customerId);

    /**
     * Update order status
     * @param orderId Order unique identifier
     * @param status New status
     * @return Updated order response
     */
    OrderDto.Response updateOrderStatus(String orderId, OrderStatus status);

    /**
     * Get orders with amount greater than specified value
     * @param amount Minimum order amount
     * @return List of order responses
     */
    List<OrderDto.Response> getOrdersWithAmountGreaterThan(BigDecimal amount);

    /**
     * Process order from message queue
     * @param orderDto Order DTO from message
     * @return Processed order response
     */
    OrderDto.Response processOrderMessage(OrderDto.Request orderDto);
}
