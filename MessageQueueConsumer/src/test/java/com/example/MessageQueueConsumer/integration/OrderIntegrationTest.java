package com.example.MessageQueueConsumer.integration;

import com.example.MessageQueueConsumer.dto.OrderDto;
import com.example.MessageQueueConsumer.dto.UserDto;
import com.example.MessageQueueConsumer.entity.OrderStatus;
import com.example.MessageQueueConsumer.repository.OrderRepository;
import com.example.MessageQueueConsumer.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    private String createdOrderId;
    private String createdUserUid;

    @BeforeEach
    void setUp() throws Exception {
        // Clean up data before each test
        orderRepository.deleteAll();
        userRepository.deleteAll();
        
        // Create a test user
        UserDto.Request userRequest = UserDto.Request.builder()
                .userName("orderTestUser")
                .email("ordertest@example.com")
                .firstName("Order")
                .lastName("Test")
                .phoneNumber("123-456-7890")
                .build();
                
        MvcResult userResult = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn();
                
        UserDto.Response userResponse = objectMapper.readValue(
                userResult.getResponse().getContentAsString(), 
                UserDto.Response.class);
                
        createdUserUid = userResponse.getUid();
        assertNotNull(createdUserUid);
        
        // Create a test order
        OrderDto.Request orderRequest = OrderDto.Request.builder()
                .orderName("Integration Test Order")
                .orderAmount(new BigDecimal("99.99"))
                .customerName("orderTestUser")
                .build();
                
        MvcResult orderResult = mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
                .andExpect(status().isCreated())
                .andReturn();
                
        OrderDto.Response orderResponse = objectMapper.readValue(
                orderResult.getResponse().getContentAsString(), 
                OrderDto.Response.class);
                
        createdOrderId = orderResponse.getOrderId();
        assertNotNull(createdOrderId);
    }

    @AfterEach
    void tearDown() {
        // Clean up data after each test
        orderRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createOrder_Success() throws Exception {
        OrderDto.Request newOrderRequest = OrderDto.Request.builder()
                .orderName("New Integration Test Order")
                .orderAmount(new BigDecimal("149.99"))
                .customerName("orderTestUser")
                .build();
                
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newOrderRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId", notNullValue()))
                .andExpect(jsonPath("$.orderName", is("New Integration Test Order")))
                .andExpect(jsonPath("$.orderAmount", is(149.99)))
                .andExpect(jsonPath("$.orderStatus", is("PENDING")));
    }

    @Test
    void getOrderById_Success() throws Exception {
        mockMvc.perform(get("/orders/{orderId}", createdOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(createdOrderId)))
                .andExpect(jsonPath("$.orderName", is("Integration Test Order")))
                .andExpect(jsonPath("$.orderAmount", is(99.99)));
    }
    
//    @Test
//    void getOrderById_NotFound() throws Exception {
//        mockMvc.perform(get("/orders/{orderId}", "non-existent-order"))
//                .andExpect(status().isNotFound());
//    }

    @Test
    void getAllOrders_Success() throws Exception {
        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].orderId", hasItem(createdOrderId)));
    }

    @Test
    void updateOrder_Success() throws Exception {
        OrderDto.UpdateRequest updateRequest = OrderDto.UpdateRequest.builder()
                .orderName("Updated Integration Test Order")
                .orderAmount(new BigDecimal("199.99"))
                .orderStatus(OrderStatus.PROCESSING)
                .build();

        mockMvc.perform(put("/orders/{orderId}", createdOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(createdOrderId)))
                .andExpect(jsonPath("$.orderName", is("Updated Integration Test Order")))
                .andExpect(jsonPath("$.orderAmount", is(199.99)))
                .andExpect(jsonPath("$.orderStatus", is("PROCESSING")));
                
        // Verify the changes were persisted
        mockMvc.perform(get("/orders/{orderId}", createdOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderName", is("Updated Integration Test Order")))
                .andExpect(jsonPath("$.orderAmount", is(199.99)))
                .andExpect(jsonPath("$.orderStatus", is("PROCESSING")));
    }

    @Test
    void getOrdersByStatus_ReturnsCorrectOrders() throws Exception {
        mockMvc.perform(get("/orders/status/{status}", OrderStatus.PENDING))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].orderId", hasItem(createdOrderId)))
                .andExpect(jsonPath("$[*].orderStatus", everyItem(is("PENDING"))));
    }

    @Test
    void updateOrderStatus_Success() throws Exception {
        mockMvc.perform(patch("/orders/{orderId}/status", createdOrderId)
                .param("status", "PROCESSING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId", is(createdOrderId)))
                .andExpect(jsonPath("$.orderStatus", is("PROCESSING")));
                
        // Verify the status change was persisted
        mockMvc.perform(get("/orders/{orderId}", createdOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderStatus", is("PROCESSING")));
    }
    
    @Test
    void deleteOrder_Success() throws Exception {
        mockMvc.perform(delete("/orders/{orderId}", createdOrderId))
                .andExpect(status().isNoContent());
                
        // Verify the order is now deleted (soft delete)
        mockMvc.perform(get("/orders/{orderId}", createdOrderId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DELETED")));
    }
    
    @Test
    void getOrdersWithAmountGreaterThan_ReturnsCorrectOrders() throws Exception {
        // Create an expensive order
        OrderDto.Request expensiveOrderRequest = OrderDto.Request.builder()
                .orderName("Expensive Test Order")
                .orderAmount(new BigDecimal("299.99"))
                .customerName("orderTestUser")
                .build();
                
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(expensiveOrderRequest)))
                .andExpect(status().isCreated());
        
        // Test the endpoint
        mockMvc.perform(get("/orders/amount/greater-than")
                .param("amount", "200.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].orderName", hasItem("Expensive Test Order")))
                .andExpect(jsonPath("$[*].orderAmount", hasItem(299.99)));
                
        // Test with amount that should exclude our standard test order
        mockMvc.perform(get("/orders/amount/greater-than")
                .param("amount", "100.00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].orderName", not(hasItem("Integration Test Order"))));
    }

    @Test
    void getOrdersByCustomerId_Success() throws Exception {
        // We need to create an order with a specific customer first
        // This assumes a customer-order relationship is established when creating orders
        
        // For this test we'll just verify that the endpoint works
        mockMvc.perform(get("/orders/customer/{customerId}", createdUserUid))
                .andExpect(status().isOk());
    }
    
    @Test
    void createOrder_ValidationFailure() throws Exception {
        OrderDto.Request invalidOrderRequest = OrderDto.Request.builder()
                .orderName("")  // Empty order name should fail validation
                .build();  // Missing required order amount
                
        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidOrderRequest)))
                .andExpect(status().isBadRequest());
    }
}