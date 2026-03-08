package com.example.MessageQueueConsumer.utils;

import com.example.MessageQueueConsumer.dto.OrderDto;
import com.example.MessageQueueConsumer.dto.UserDto;
import com.example.MessageQueueConsumer.entity.Order;
import com.example.MessageQueueConsumer.entity.OrderStatus;
import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Utility class for test data generation and manipulation
 */
public class TestUtils {
    
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Creates a test User entity
     * 
     * @param username The username
     * @return A test User entity
     */
    public static User createTestUser(String username) {
        User user = new User();
        user.setUid(UUID.randomUUID().toString().replace("-", ""));
        user.setUserName(username);
        user.setEmail(username + "@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhoneNumber("123-456-7890");
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(Instant.now());
        return user;
    }
    
    /**
     * Creates a test Order entity
     * 
     * @param orderName The order name
     * @param amount The order amount
     * @param customer The customer user
     * @return A test Order entity
     */
    public static Order createTestOrder(String orderName, BigDecimal amount, User customer) {
        Order order = new Order();
        order.setOrderId(UUID.randomUUID().toString().replace("-", ""));
        order.setOrderName(orderName);
        order.setOrderAmount(amount);
        order.setCustomer(customer);
        order.setOrderDate(Instant.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setStatus(Status.ACTIVE);
        order.setCreatedAt(Instant.now());
        return order;
    }
    
    /**
     * Creates a test UserDto.Request
     * 
     * @param username The username
     * @return A test UserDto.Request
     */
    public static UserDto.Request createTestUserRequest(String username) {
        return UserDto.Request.builder()
                .userName(username)
                .email(username + "@example.com")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("123-456-7890")
                .build();
    }
    
    /**
     * Creates a test OrderDto.Request
     * 
     * @param orderName The order name
     * @param amount The order amount
     * @param customerName The customer name
     * @return A test OrderDto.Request
     */
    public static OrderDto.Request createTestOrderRequest(String orderName, BigDecimal amount, String customerName) {
        return OrderDto.Request.builder()
                .orderName(orderName)
                .orderAmount(amount)
                .customerName(customerName)
                .build();
    }
    
    /**
     * Converts an object to JSON string
     * 
     * @param object The object to convert
     * @return JSON string representation
     */
    public static String asJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Generates a random valid order ID
     * 
     * @return A random order ID
     */
    public static String randomOrderId() {
        return "order-" + UUID.randomUUID().toString().substring(0, 8);
    }
    
    /**
     * Generates a random valid user UID
     * 
     * @return A random user UID
     */
    public static String randomUserUid() {
        return "user-" + UUID.randomUUID().toString().substring(0, 8);
    }
}