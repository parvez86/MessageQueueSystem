package com.example.MessageQueueConsumer.controller;

import com.example.MessageQueueConsumer.dto.OrderDto;
import com.example.MessageQueueConsumer.entity.OrderStatus;
import com.example.MessageQueueConsumer.service.OrderService;
import com.example.MessageQueueConsumer.util.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create a new order")
    @PostMapping
    public ResponseEntity<OrderDto.Response> createOrder(
            @Valid @RequestBody OrderDto.Request request) {
        log.info("Creating new order: {}", request);
        return ResponseEntity.status(HttpStatus.CREATED).body(orderService.createOrder(request));
    }

    @Operation(summary = "Get order by ID")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto.Response> getOrderById(
            @Parameter(description = "Order unique identifier", required = true)
            @PathVariable String orderId) {
        log.info("Fetching order with ID: {}", orderId);
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }

    @Operation(summary = "Get all orders with pagination")
    @GetMapping
    public ResponseEntity<Page<OrderDto.Response>> getAllOrders(Pageable pageable) {
        log.info("Fetching all orders with pagination: {}", pageable);
        return ResponseEntity.ok(orderService.getAllOrders(pageable));
    }

    @Operation(summary = "Update order")
    @PutMapping("/{orderId}")
    public ResponseEntity<OrderDto.Response> updateOrder(
            @Parameter(description = "Order unique identifier", required = true)
            @PathVariable String orderId,
            @Valid @RequestBody OrderDto.UpdateRequest request) {
        log.info("Updating order with ID: {}", orderId);
        return ResponseEntity.ok(orderService.updateOrder(orderId, request));
    }

    @Operation(summary = "Delete order")
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(
            @Parameter(description = "Order unique identifier", required = true)
            @PathVariable String orderId) {
        log.info("Deleting order with ID: {}", orderId);
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get orders by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<OrderDto.Response>> getOrdersByStatus(
            @Parameter(description = "Order status", required = true)
            @PathVariable OrderStatus status) {
        log.info("Fetching orders with status: {}", status);
        return ResponseEntity.ok(orderService.getOrdersByStatus(status));
    }

    @Operation(summary = "Get orders by customer ID")
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<OrderDto.Response>> getOrdersByCustomerId(
            @Parameter(description = "Customer unique identifier", required = true)
            @PathVariable String customerId) {
        log.info("Fetching orders for customer: {}", customerId);
        return ResponseEntity.ok(orderService.getOrdersByCustomerId(customerId));
    }

    @Operation(summary = "Update order status")
    @PatchMapping("/{orderId}/status")
    public ResponseEntity<OrderDto.Response> updateOrderStatus(
            @Parameter(description = "Order unique identifier", required = true)
            @PathVariable String orderId,
            @Parameter(description = "New status", required = true)
            @RequestParam OrderStatus status) {
        log.info("Updating status of order with ID: {} to {}", orderId, status);
        return ResponseEntity.ok(orderService.updateOrderStatus(orderId, status));
    }

    @Operation(summary = "Get orders with amount greater than specified value")
    @GetMapping("/amount/greater-than")
    public ResponseEntity<List<OrderDto.Response>> getOrdersWithAmountGreaterThan(
            @Parameter(description = "Minimum order amount", required = true)
            @RequestParam BigDecimal amount) {
        log.info("Fetching orders with amount greater than: {}", amount);
        return ResponseEntity.ok(orderService.getOrdersWithAmountGreaterThan(amount));
    }

    @Operation(summary = "Process order message from RabbitMQ")
    @RabbitListener(queues = MessageUtil.ORDER_QUEUE)
    public void processOrderMessage(OrderDto.Request orderDto) {
        log.info("Received order message: {}", orderDto);
        orderService.processOrderMessage(orderDto);
    }
}