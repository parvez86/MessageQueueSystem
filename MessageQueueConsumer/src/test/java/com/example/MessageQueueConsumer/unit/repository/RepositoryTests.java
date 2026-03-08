package com.example.MessageQueueConsumer.unit.repository;

import com.example.MessageQueueConsumer.entity.Order;
import com.example.MessageQueueConsumer.entity.OrderStatus;
import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.entity.User;
import com.example.MessageQueueConsumer.repository.OrderRepository;
import com.example.MessageQueueConsumer.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class RepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void userRepository_findByUid_Success() {
        // Arrange
        User user = new User();
        user.setUid("test-uid-123");
        user.setUserName("testuser");
        user.setEmail("test@example.com");
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(Instant.now());
        
        entityManager.persist(user);
        entityManager.flush();

        // Act
        Optional<User> foundUser = userRepository.findByUid("test-uid-123");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUserName());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void userRepository_findByStatus_ReturnsCorrectUsers() {
        // Arrange
        User activeUser = new User();
        activeUser.setUid("active-uid-123");
        activeUser.setUserName("activeuser");
        activeUser.setEmail("active@example.com");
        activeUser.setStatus(Status.ACTIVE);
        activeUser.setCreatedAt(Instant.now());
        
        User inactiveUser = new User();
        inactiveUser.setUid("inactive-uid-456");
        inactiveUser.setUserName("inactiveuser");
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setStatus(Status.INACTIVE);
        inactiveUser.setCreatedAt(Instant.now());
        
        entityManager.persist(activeUser);
        entityManager.persist(inactiveUser);
        entityManager.flush();

        // Act
        List<User> activeUsers = userRepository.findByStatus(Status.ACTIVE);
        List<User> inactiveUsers = userRepository.findByStatus(Status.INACTIVE);

        // Assert
        assertEquals(1, activeUsers.size());
        assertEquals("activeuser", activeUsers.get(0).getUserName());
        
        assertEquals(1, inactiveUsers.size());
        assertEquals("inactiveuser", inactiveUsers.get(0).getUserName());
    }

    @Test
    void userRepository_search_ReturnsMatchingUsers() {
        // Arrange
        User user1 = new User();
        user1.setUid("uid-123");
        user1.setUserName("testuser");
        user1.setEmail("test@example.com");
        user1.setStatus(Status.ACTIVE);
        user1.setCreatedAt(Instant.now());
        
        User user2 = new User();
        user2.setUid("uid-456");
        user2.setUserName("otheruser");
        user2.setEmail("other@example.com");
        user2.setStatus(Status.ACTIVE);
        user2.setCreatedAt(Instant.now());
        
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        // Act
        List<User> searchByName = userRepository
                .findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase("test", "test");
        
        List<User> searchByEmail = userRepository
                .findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase("other", "other");

        // Assert
        assertEquals(1, searchByName.size());
        assertEquals("testuser", searchByName.get(0).getUserName());
        
        assertEquals(1, searchByEmail.size());
        assertEquals("otheruser", searchByEmail.get(0).getUserName());
    }

    @Test
    void orderRepository_findByOrderId_Success() {
        // Arrange
        User customer = new User();
        customer.setUid("customer-123");
        customer.setUserName("customer");
        customer.setEmail("customer@example.com");
        customer.setStatus(Status.ACTIVE);
        customer.setCreatedAt(Instant.now());
        
        entityManager.persist(customer);
        
        Order order = new Order();
        order.setOrderId("order-123");
        order.setOrderName("Test Order");
        order.setOrderAmount(new BigDecimal("99.99"));
        order.setCustomer(customer);
        order.setOrderDate(Instant.now());
        order.setOrderStatus(OrderStatus.PENDING);
        order.setStatus(Status.ACTIVE);
        order.setCreatedAt(Instant.now());
        
        entityManager.persist(order);
        entityManager.flush();

        // Act
        Optional<Order> foundOrder = orderRepository.findByOrderId("order-123");

        // Assert
        assertTrue(foundOrder.isPresent());
        assertEquals("Test Order", foundOrder.get().getOrderName());
        assertEquals(new BigDecimal("99.99"), foundOrder.get().getOrderAmount());
        assertEquals("customer-123", foundOrder.get().getCustomer().getUid());
    }

    @Test
    void orderRepository_findByOrderStatus_ReturnsCorrectOrders() {
        // Arrange
        User customer = new User();
        customer.setUid("customer-123");
        customer.setUserName("customer");
        customer.setEmail("customer@example.com");
        customer.setStatus(Status.ACTIVE);
        customer.setCreatedAt(Instant.now());
        
        entityManager.persist(customer);
        
        Order pendingOrder = new Order();
        pendingOrder.setOrderId("pending-123");
        pendingOrder.setOrderName("Pending Order");
        pendingOrder.setOrderAmount(new BigDecimal("99.99"));
        pendingOrder.setCustomer(customer);
        pendingOrder.setOrderDate(Instant.now());
        pendingOrder.setOrderStatus(OrderStatus.PENDING);
        pendingOrder.setStatus(Status.ACTIVE);
        pendingOrder.setCreatedAt(Instant.now());
        
        Order completedOrder = new Order();
        completedOrder.setOrderId("completed-456");
        completedOrder.setOrderName("Completed Order");
        completedOrder.setOrderAmount(new BigDecimal("149.99"));
        completedOrder.setCustomer(customer);
        completedOrder.setOrderDate(Instant.now());
        completedOrder.setOrderStatus(OrderStatus.COMPLETED);
        completedOrder.setStatus(Status.ACTIVE);
        completedOrder.setCreatedAt(Instant.now());
        
        entityManager.persist(pendingOrder);
        entityManager.persist(completedOrder);
        entityManager.flush();

        // Act
        List<Order> pendingOrders = orderRepository.findByOrderStatus(OrderStatus.PENDING);
        List<Order> completedOrders = orderRepository.findByOrderStatus(OrderStatus.COMPLETED);

        // Assert
        assertEquals(1, pendingOrders.size());
        assertEquals("Pending Order", pendingOrders.get(0).getOrderName());
        
        assertEquals(1, completedOrders.size());
        assertEquals("Completed Order", completedOrders.get(0).getOrderName());
    }

    @Test
    void orderRepository_findByCustomer_ReturnsCorrectOrders() {
        // Arrange
        User customer1 = new User();
        customer1.setUid("customer-123");
        customer1.setUserName("customer1");
        customer1.setEmail("customer1@example.com");
        customer1.setStatus(Status.ACTIVE);
        customer1.setCreatedAt(Instant.now());
        
        User customer2 = new User();
        customer2.setUid("customer-456");
        customer2.setUserName("customer2");
        customer2.setEmail("customer2@example.com");
        customer2.setStatus(Status.ACTIVE);
        customer2.setCreatedAt(Instant.now());
        
        entityManager.persist(customer1);
        entityManager.persist(customer2);
        
        Order order1 = new Order();
        order1.setOrderId("order-123");
        order1.setOrderName("Order for Customer 1");
        order1.setOrderAmount(new BigDecimal("99.99"));
        order1.setCustomer(customer1);
        order1.setOrderDate(Instant.now());
        order1.setOrderStatus(OrderStatus.PENDING);
        order1.setStatus(Status.ACTIVE);
        order1.setCreatedAt(Instant.now());
        
        Order order2 = new Order();
        order2.setOrderId("order-456");
        order2.setOrderName("Order for Customer 2");
        order2.setOrderAmount(new BigDecimal("149.99"));
        order2.setCustomer(customer2);
        order2.setOrderDate(Instant.now());
        order2.setOrderStatus(OrderStatus.PENDING);
        order2.setStatus(Status.ACTIVE);
        order2.setCreatedAt(Instant.now());
        
        entityManager.persist(order1);
        entityManager.persist(order2);
        entityManager.flush();

        // Act
        List<Order> customer1Orders = orderRepository.findByCustomer(customer1);
        List<Order> customer2Orders = orderRepository.findByCustomer(customer2);

        // Assert
        assertEquals(1, customer1Orders.size());
        assertEquals("Order for Customer 1", customer1Orders.get(0).getOrderName());
        
        assertEquals(1, customer2Orders.size());
        assertEquals("Order for Customer 2", customer2Orders.get(0).getOrderName());
    }

    @Test
    void orderRepository_findByOrderAmountGreaterThan_ReturnsCorrectOrders() {
        // Arrange
        User customer = new User();
        customer.setUid("customer-123");
        customer.setUserName("customer");
        customer.setEmail("customer@example.com");
        customer.setStatus(Status.ACTIVE);
        customer.setCreatedAt(Instant.now());
        
        entityManager.persist(customer);
        
        Order cheapOrder = new Order();
        cheapOrder.setOrderId("cheap-123");
        cheapOrder.setOrderName("Cheap Order");
        cheapOrder.setOrderAmount(new BigDecimal("49.99"));
        cheapOrder.setCustomer(customer);
        cheapOrder.setOrderDate(Instant.now());
        cheapOrder.setOrderStatus(OrderStatus.PENDING);
        cheapOrder.setStatus(Status.ACTIVE);
        cheapOrder.setCreatedAt(Instant.now());
        
        Order expensiveOrder = new Order();
        expensiveOrder.setOrderId("expensive-456");
        expensiveOrder.setOrderName("Expensive Order");
        expensiveOrder.setOrderAmount(new BigDecimal("149.99"));
        expensiveOrder.setCustomer(customer);
        expensiveOrder.setOrderDate(Instant.now());
        expensiveOrder.setOrderStatus(OrderStatus.PENDING);
        expensiveOrder.setStatus(Status.ACTIVE);
        expensiveOrder.setCreatedAt(Instant.now());
        
        entityManager.persist(cheapOrder);
        entityManager.persist(expensiveOrder);
        entityManager.flush();

        // Act
        List<Order> expensiveOrders = orderRepository.findByOrderAmountGreaterThan(new BigDecimal("100.00"));
        List<Order> allOrders = orderRepository.findByOrderAmountGreaterThan(new BigDecimal("0.00"));

        // Assert
        assertEquals(1, expensiveOrders.size());
        assertEquals("Expensive Order", expensiveOrders.get(0).getOrderName());
        
        assertEquals(2, allOrders.size());
    }
}