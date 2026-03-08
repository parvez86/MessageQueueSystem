package com.example.MessageQueueConsumer.unit.repository;

import com.example.MessageQueueConsumer.entity.Order;
import com.example.MessageQueueConsumer.entity.OrderStatus;
import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.entity.User;
import com.example.MessageQueueConsumer.repository.OrderRepository;
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
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository orderRepository;

    @Test
    void findByOrderId_Success() {
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
    void findByOrderId_NotFound_ReturnsEmpty() {
        // Act
        Optional<Order> foundOrder = orderRepository.findByOrderId("non-existent-order");

        // Assert
        assertFalse(foundOrder.isPresent());
    }

    @Test
    void findByOrderStatus_ReturnsCorrectOrders() {
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
        List<Order> cancelledOrders = orderRepository.findByOrderStatus(OrderStatus.CANCELLED);

        // Assert
        assertTrue(pendingOrders.size() >= 1);
        assertTrue(pendingOrders.stream().anyMatch(o -> "Pending Order".equals(o.getOrderName())));
        
        assertTrue(completedOrders.size() >= 1);
        assertTrue(completedOrders.stream().anyMatch(o -> "Completed Order".equals(o.getOrderName())));
        
        assertEquals(0, cancelledOrders.size());
    }

    @Test
    void findByCustomer_ReturnsCorrectOrders() {
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
    void findByOrderAmountGreaterThan_ReturnsCorrectOrders() {
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
        List<Order> noOrders = orderRepository.findByOrderAmountGreaterThan(new BigDecimal("1000.00"));

        // Assert
        assertTrue(expensiveOrders.size() >= 1);
        assertTrue(expensiveOrders.stream().anyMatch(o -> "Expensive Order".equals(o.getOrderName())));
        assertFalse(expensiveOrders.stream().anyMatch(o -> "Cheap Order".equals(o.getOrderName())));
        
        assertTrue(allOrders.size() >= 2);
        assertTrue(allOrders.stream().anyMatch(o -> "Expensive Order".equals(o.getOrderName())));
        assertTrue(allOrders.stream().anyMatch(o -> "Cheap Order".equals(o.getOrderName())));
        
        assertEquals(0, noOrders.size());
    }
    
    @Test
    void findByCustomerAndOrderStatus_ReturnsCorrectOrders() {
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
        List<Order> customerPendingOrders = orderRepository.findByCustomerAndOrderStatus(customer, OrderStatus.PENDING);
        List<Order> customerCompletedOrders = orderRepository.findByCustomerAndOrderStatus(customer, OrderStatus.COMPLETED);
        List<Order> customerCancelledOrders = orderRepository.findByCustomerAndOrderStatus(customer, OrderStatus.CANCELLED);

        // Assert
        assertEquals(1, customerPendingOrders.size());
        assertEquals("Pending Order", customerPendingOrders.get(0).getOrderName());
        
        assertEquals(1, customerCompletedOrders.size());
        assertEquals("Completed Order", customerCompletedOrders.get(0).getOrderName());
        
        assertEquals(0, customerCancelledOrders.size());
    }
    
    @Test
    void countByOrderStatus_ReturnsCorrectCount() {
        // Arrange
        User customer = new User();
        customer.setUid("customer-123");
        customer.setUserName("customer");
        customer.setEmail("customer@example.com");
        customer.setStatus(Status.ACTIVE);
        customer.setCreatedAt(Instant.now());
        
        entityManager.persist(customer);
        
        Order pendingOrder1 = new Order();
        pendingOrder1.setOrderId("pending-123");
        pendingOrder1.setOrderName("Pending Order 1");
        pendingOrder1.setOrderAmount(new BigDecimal("99.99"));
        pendingOrder1.setCustomer(customer);
        pendingOrder1.setOrderDate(Instant.now());
        pendingOrder1.setOrderStatus(OrderStatus.PENDING);
        pendingOrder1.setStatus(Status.ACTIVE);
        pendingOrder1.setCreatedAt(Instant.now());
        
        Order pendingOrder2 = new Order();
        pendingOrder2.setOrderId("pending-456");
        pendingOrder2.setOrderName("Pending Order 2");
        pendingOrder2.setOrderAmount(new BigDecimal("129.99"));
        pendingOrder2.setCustomer(customer);
        pendingOrder2.setOrderDate(Instant.now());
        pendingOrder2.setOrderStatus(OrderStatus.PENDING);
        pendingOrder2.setStatus(Status.ACTIVE);
        pendingOrder2.setCreatedAt(Instant.now());
        
        Order completedOrder = new Order();
        completedOrder.setOrderId("completed-789");
        completedOrder.setOrderName("Completed Order");
        completedOrder.setOrderAmount(new BigDecimal("149.99"));
        completedOrder.setCustomer(customer);
        completedOrder.setOrderDate(Instant.now());
        completedOrder.setOrderStatus(OrderStatus.COMPLETED);
        completedOrder.setStatus(Status.ACTIVE);
        completedOrder.setCreatedAt(Instant.now());
        
        entityManager.persist(pendingOrder1);
        entityManager.persist(pendingOrder2);
        entityManager.persist(completedOrder);
        entityManager.flush();

        // Act
        long pendingCount = orderRepository.countByOrderStatus(OrderStatus.PENDING);
        long completedCount = orderRepository.countByOrderStatus(OrderStatus.COMPLETED);
        long cancelledCount = orderRepository.countByOrderStatus(OrderStatus.CANCELLED);

        // Assert
        assertTrue(pendingCount >= 2);
        assertTrue(completedCount >= 1);
        assertEquals(0, cancelledCount);
    }
}