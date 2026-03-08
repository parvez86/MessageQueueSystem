package com.example.MessageQueueConsumer.unit.service;

import com.example.MessageQueueConsumer.dto.OrderDto;
import com.example.MessageQueueConsumer.entity.Order;
import com.example.MessageQueueConsumer.entity.OrderStatus;
import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.entity.User;
import com.example.MessageQueueConsumer.repository.OrderRepository;
import com.example.MessageQueueConsumer.repository.UserRepository;
import com.example.MessageQueueConsumer.service.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderServiceImpl orderService;

    private OrderDto.Request orderRequest;
    private OrderDto.UpdateRequest updateRequest;
    private Order order;
    private User user;

    @BeforeEach
    void setUp() {
        orderRequest = OrderDto.Request.builder()
                .orderName("Test Order")
                .orderAmount(new BigDecimal("99.99"))
                .customerName("Test Customer")
                .build();

        updateRequest = OrderDto.UpdateRequest.builder()
                .orderName("Updated Order")
                .orderAmount(new BigDecimal("149.99"))
                .orderStatus(OrderStatus.PROCESSING)
                .build();

        user = new User();
        user.setId(1L);
        user.setUid("customer-456");
        user.setUserName("testcustomer");
        user.setEmail("customer@example.com");
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(Instant.now());

        order = new Order();
        order.setId(1L);
        order.setOrderId("order-123");
        order.setOrderName("Test Order");
        order.setOrderAmount(new BigDecimal("99.99"));
        order.setCustomer(user);
        order.setOrderDate(Instant.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setStatus(Status.ACTIVE);
        order.setCreatedAt(Instant.now());
        order.setUpdatedAt(null);
    }

    @Test
    void createOrder_Success() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDto.Response response = orderService.createOrder(orderRequest);

        assertNotNull(response);
        assertEquals("order-123", response.getOrderId());
        assertEquals("Test Order", response.getOrderName());
        assertEquals(new BigDecimal("99.99"), response.getOrderAmount());
        assertEquals(OrderStatus.PENDING, response.getOrderStatus());

        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getOrderById_Success() {
        when(orderRepository.findByOrderId("order-123")).thenReturn(Optional.of(order));

        OrderDto.Response response = orderService.getOrderById("order-123");

        assertNotNull(response);
        assertEquals("order-123", response.getOrderId());
        assertEquals("Test Order", response.getOrderName());

        verify(orderRepository).findByOrderId("order-123");
    }

    @Test
    void getOrderById_NotFound() {
        when(orderRepository.findByOrderId("non-existent-order")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> orderService.getOrderById("non-existent-order"));

        verify(orderRepository).findByOrderId("non-existent-order");
    }

    @Test
    void getAllOrders_Success() {
        List<Order> orders = Arrays.asList(order);
        Page<Order> page = new PageImpl<>(orders);
        Pageable pageable = PageRequest.of(0, 10);

        when(orderRepository.findAll(pageable)).thenReturn(page);

        Page<OrderDto.Response> response = orderService.getAllOrders(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("order-123", response.getContent().get(0).getOrderId());

        verify(orderRepository).findAll(pageable);
    }

    @Test
    void updateOrder_Success() {
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setOrderId("order-123");
        updatedOrder.setOrderName("Updated Order");
        updatedOrder.setOrderAmount(new BigDecimal("149.99"));
        updatedOrder.setCustomer(user);
        updatedOrder.setOrderDate(Instant.now());
        updatedOrder.setOrderStatus(OrderStatus.PROCESSING);
        updatedOrder.setStatus(Status.ACTIVE);
        updatedOrder.setCreatedAt(Instant.now());
        updatedOrder.setUpdatedAt(Instant.now());

        when(orderRepository.findByOrderId("order-123")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        OrderDto.Response response = orderService.updateOrder("order-123", updateRequest);

        assertNotNull(response);
        assertEquals("order-123", response.getOrderId());
        assertEquals("Updated Order", response.getOrderName());
        assertEquals(new BigDecimal("149.99"), response.getOrderAmount());
        assertEquals(OrderStatus.PROCESSING, response.getOrderStatus());

        verify(orderRepository).findByOrderId("order-123");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void deleteOrder_Success() {
        when(orderRepository.findByOrderId("order-123")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        orderService.deleteOrder("order-123");

        verify(orderRepository).findByOrderId("order-123");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getOrdersByStatus_Success() {
        List<Order> pendingOrders = Arrays.asList(order);
        when(orderRepository.findByOrderStatus(OrderStatus.PENDING)).thenReturn(pendingOrders);

        List<OrderDto.Response> response = orderService.getOrdersByStatus(OrderStatus.PENDING);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("order-123", response.get(0).getOrderId());

        verify(orderRepository).findByOrderStatus(OrderStatus.PENDING);
    }

    @Test
    void getOrdersByCustomerId_Success() {
        List<Order> customerOrders = Arrays.asList(order);
        
        when(userRepository.findByUid("customer-456")).thenReturn(Optional.of(user));
        when(orderRepository.findByCustomer(user)).thenReturn(customerOrders);

        List<OrderDto.Response> response = orderService.getOrdersByCustomerId("customer-456");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("order-123", response.get(0).getOrderId());

        verify(userRepository).findByUid("customer-456");
        verify(orderRepository).findByCustomer(user);
    }

    @Test
    void getOrdersByCustomerId_CustomerNotFound() {
        when(userRepository.findByUid("non-existent-customer")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                    () -> orderService.getOrdersByCustomerId("non-existent-customer"));

        verify(userRepository).findByUid("non-existent-customer");
        verify(orderRepository, never()).findByCustomer(any(User.class));
    }

    @Test
    void updateOrderStatus_Success() {
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setOrderId("order-123");
        updatedOrder.setOrderName("Test Order");
        updatedOrder.setOrderAmount(new BigDecimal("99.99"));
        updatedOrder.setCustomer(user);
        updatedOrder.setOrderDate(Instant.now());
        updatedOrder.setOrderStatus(OrderStatus.PROCESSING);
        updatedOrder.setStatus(Status.ACTIVE);
        updatedOrder.setCreatedAt(Instant.now());
        updatedOrder.setUpdatedAt(Instant.now());

        when(orderRepository.findByOrderId("order-123")).thenReturn(Optional.of(order));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        OrderDto.Response response = orderService.updateOrderStatus("order-123", OrderStatus.PROCESSING);

        assertNotNull(response);
        assertEquals("order-123", response.getOrderId());
        assertEquals(OrderStatus.PROCESSING, response.getOrderStatus());

        verify(orderRepository).findByOrderId("order-123");
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void getOrdersWithAmountGreaterThan_Success() {
        List<Order> expensiveOrders = Arrays.asList(order);
        when(orderRepository.findByOrderAmountGreaterThan(new BigDecimal("50.00"))).thenReturn(expensiveOrders);

        List<OrderDto.Response> response = orderService.getOrdersWithAmountGreaterThan(new BigDecimal("50.00"));

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("order-123", response.get(0).getOrderId());

        verify(orderRepository).findByOrderAmountGreaterThan(new BigDecimal("50.00"));
    }

    @Test
    void processOrderMessage_Success() {
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderDto.Response response = orderService.processOrderMessage(orderRequest);

        assertNotNull(response);
        assertEquals("order-123", response.getOrderId());
        
        verify(orderRepository).save(any(Order.class));
    }
}