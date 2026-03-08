package com.example.MessageQueueConsumer.unit.controller;

import com.example.MessageQueueConsumer.controller.OrderController;
import com.example.MessageQueueConsumer.dto.OrderDto;
import com.example.MessageQueueConsumer.entity.OrderStatus;
import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.service.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
public class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ObjectMapper objectMapper;

    private OrderDto.Request orderRequest;
    private OrderDto.Response orderResponse;

    @BeforeEach
    void setUp() {
        orderRequest = OrderDto.Request.builder()
                .orderName("Test Order")
                .orderAmount(new BigDecimal("99.99"))
                .customerName("Test Customer")
                .build();

        orderResponse = OrderDto.Response.builder()
                .orderId("order-123")
                .orderName("Test Order")
                .orderAmount(new BigDecimal("99.99"))
                .customerUid("customer-456")
                .customerName("Test Customer")
                .orderDate(Instant.now())
                .orderStatus(OrderStatus.PENDING)
                .status(Status.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createOrder_Success() throws Exception {
        when(orderService.createOrder(any(OrderDto.Request.class))).thenReturn(orderResponse);

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", is("order-123")))
                .andExpect(jsonPath("$.orderName", is("Test Order")))
                .andExpect(jsonPath("$.orderAmount", is(99.99)));

        verify(orderService).createOrder(any(OrderDto.Request.class));
    }

    @Test
    void getOrderById_Success() throws Exception {
        when(orderService.getOrderById("order-123")).thenReturn(orderResponse);

        mockMvc.perform(get("/orders/order-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is("order-123")))
                .andExpect(jsonPath("$.orderName", is("Test Order")));

        verify(orderService).getOrderById("order-123");
    }

    @Test
    void getAllOrders_Success() throws Exception {
        List<OrderDto.Response> orders = Arrays.asList(orderResponse);
        Page<OrderDto.Response> page = new PageImpl<>(orders);
        
        when(orderService.getAllOrders(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].orderId", is("order-123")));

        verify(orderService).getAllOrders(any(Pageable.class));
    }

    @Test
    void updateOrder_Success() throws Exception {
        OrderDto.UpdateRequest updateRequest = OrderDto.UpdateRequest.builder()
                .orderName("Updated Order")
                .orderAmount(new BigDecimal("149.99"))
                .build();

        OrderDto.Response updatedResponse = OrderDto.Response.builder()
                .orderId("order-123")
                .orderName("Updated Order")
                .orderAmount(new BigDecimal("149.99"))
                .customerUid("customer-456")
                .customerName("Test Customer")
                .orderDate(Instant.now())
                .orderStatus(OrderStatus.PENDING)
                .status(Status.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(orderService.updateOrder(eq("order-123"), any(OrderDto.UpdateRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/orders/order-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is("order-123")))
                .andExpect(jsonPath("$.orderName", is("Updated Order")))
                .andExpect(jsonPath("$.orderAmount", is(149.99)));

        verify(orderService).updateOrder(eq("order-123"), any(OrderDto.UpdateRequest.class));
    }

    @Test
    void deleteOrder_Success() throws Exception {
        doNothing().when(orderService).deleteOrder("order-123");

        mockMvc.perform(delete("/orders/order-123"))
                .andExpect(status().isNoContent());

        verify(orderService).deleteOrder("order-123");
    }

    @Test
    void getOrdersByStatus_Success() throws Exception {
        List<OrderDto.Response> pendingOrders = Arrays.asList(orderResponse);
        when(orderService.getOrdersByStatus(OrderStatus.PENDING)).thenReturn(pendingOrders);

        mockMvc.perform(get("/orders/status/PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orderId", is("order-123")));

        verify(orderService).getOrdersByStatus(OrderStatus.PENDING);
    }

    @Test
    void getOrdersByCustomerId_Success() throws Exception {
        List<OrderDto.Response> customerOrders = Arrays.asList(orderResponse);
        when(orderService.getOrdersByCustomerId("customer-456")).thenReturn(customerOrders);

        mockMvc.perform(get("/orders/customer/customer-456"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orderId", is("order-123")));

        verify(orderService).getOrdersByCustomerId("customer-456");
    }

    @Test
    void updateOrderStatus_Success() throws Exception {
        OrderDto.Response updatedResponse = OrderDto.Response.builder()
                .orderId("order-123")
                .orderName("Test Order")
                .orderAmount(new BigDecimal("99.99"))
                .customerUid("customer-456")
                .customerName("Test Customer")
                .orderDate(Instant.now())
                .orderStatus(OrderStatus.PROCESSING)
                .status(Status.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(orderService.updateOrderStatus("order-123", OrderStatus.PROCESSING)).thenReturn(updatedResponse);

        mockMvc.perform(patch("/orders/order-123/status")
                .param("status", "PROCESSING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is("order-123")))
                .andExpect(jsonPath("$.orderStatus", is("PROCESSING")));

        verify(orderService).updateOrderStatus("order-123", OrderStatus.PROCESSING);
    }

    @Test
    void getOrdersWithAmountGreaterThan_Success() throws Exception {
        List<OrderDto.Response> expensiveOrders = Arrays.asList(orderResponse);
        when(orderService.getOrdersWithAmountGreaterThan(new BigDecimal("50.00"))).thenReturn(expensiveOrders);

        mockMvc.perform(get("/orders/amount/greater-than")
                .param("amount", "50.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].orderId", is("order-123")));

        verify(orderService).getOrdersWithAmountGreaterThan(new BigDecimal("50.00"));
    }

    @Test
    void processOrderMessage_Success() throws Exception {
        // Testing RabbitMQ listener method
        doNothing().when(orderService).processOrderMessage(any(OrderDto.Request.class));

        // Direct method invocation since it's not a REST endpoint
        OrderController controller = new OrderController(orderService);
        controller.processOrderMessage(orderRequest);

        verify(orderService).processOrderMessage(any(OrderDto.Request.class));
    }
}