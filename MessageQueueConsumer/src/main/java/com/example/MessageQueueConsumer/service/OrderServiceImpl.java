package com.example.MessageQueueConsumer.service;

import com.example.MessageQueueConsumer.dto.OrderDto;
import com.example.MessageQueueConsumer.entity.Order;
import com.example.MessageQueueConsumer.entity.OrderStatus;
import com.example.MessageQueueConsumer.entity.User;
import com.example.MessageQueueConsumer.repository.OrderRepository;
import com.example.MessageQueueConsumer.repository.UserRepository;
import com.example.MessageQueueConsumer.util.AppUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService{
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public OrderDto.Response createOrder(OrderDto.Request request) {
        log.info("Creating new order: {}", request);
        User user = userRepository.findByUserName(request.getCustomerName())
                .orElseThrow(() -> AppUtils.throwException("Customer not found by the name: "+request.getCustomerName()));
        Order order = OrderDto.Request.mapToEntity(request, user);
        Order savedOrder = orderRepository.save(order);
        log.info("Order created successfully: {}", savedOrder.getOrderId());
        return OrderDto.Response.mapToDto(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderDto.Response getOrderById(String orderId) {
        log.info("Fetching order with ID: {}", orderId);
        Order order = findOrderById(orderId);
        return OrderDto.Response.mapToDto(order);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<OrderDto.Response> getAllOrders(Pageable pageable) {
        log.info("Fetching all orders with pagination: {}", pageable);
        return orderRepository.findAll(pageable)
                .map(OrderDto.Response::mapToDto);
    }

    @Override
    @Transactional
    public OrderDto.Response updateOrder(String orderId, OrderDto.UpdateRequest request) {
        log.info("Updating order with ID: {}", orderId);
        Order order = findOrderById(orderId);
        OrderDto.UpdateRequest.updateEntity(request, order);
        Order updatedOrder = orderRepository.save(order);
        log.info("Order updated successfully: {}", orderId);
        return OrderDto.Response.mapToDto(updatedOrder);
    }

    @Override
    @Transactional
    public void deleteOrder(String orderId) {
        log.info("Deleting order with ID: {}", orderId);
        Order order = findOrderById(orderId);
        order.softDelete();
        orderRepository.save(order);
        log.info("Order soft deleted successfully: {}", orderId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto.Response> getOrdersByStatus(OrderStatus status) {
        log.info("Fetching orders with status: {}", status);
        List<Order> orders = orderRepository.findByOrderStatus(status);
        return orders.stream()
                .map(OrderDto.Response::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto.Response> getOrdersByCustomerId(String customerId) {
        log.info("Fetching orders for customer: {}", customerId);
        User customer = userRepository.findByUid(customerId)
                .orElseThrow(() -> AppUtils.throwException("Customer not found with ID: " + customerId));
        List<Order> orders = orderRepository.findByCustomer(customer);
        return orders.stream()
                .map(OrderDto.Response::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto.Response updateOrderStatus(String orderId, OrderStatus status) {
        log.info("Updating status of order with ID: {} to {}", orderId, status);
        Order order = findOrderById(orderId);
        order.setOrderStatus(status);
        order.setUpdatedAt(Instant.now());
        Order updatedOrder = orderRepository.save(order);
        log.info("Order status updated successfully: {}", orderId);
        return OrderDto.Response.mapToDto(updatedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderDto.Response> getOrdersWithAmountGreaterThan(BigDecimal amount) {
        log.info("Fetching orders with amount greater than: {}", amount);
        List<Order> orders = orderRepository.findByOrderAmountGreaterThan(amount);
        return orders.stream()
                .map(OrderDto.Response::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public OrderDto.Response processOrderMessage(OrderDto.Request orderDto) {
        log.info("Processing order message: {}", orderDto);
        return createOrder(orderDto);
    }

    private Order findOrderById(String orderId) {
        return orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> AppUtils.throwException("Order not found with ID: " + orderId));
    }
}
