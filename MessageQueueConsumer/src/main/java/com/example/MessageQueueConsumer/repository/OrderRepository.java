package com.example.MessageQueueConsumer.repository;
import com.example.MessageQueueConsumer.entity.Order;
import com.example.MessageQueueConsumer.entity.OrderStatus;
import com.example.MessageQueueConsumer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    /**
     * Find order by order ID
     * @param orderId Order unique identifier
     * @return Optional of Order
     */
    Optional<Order> findByOrderId(String orderId);

    /**
     * Find orders by order status
     * @param orderStatus Order status
     * @return List of orders
     */
    List<Order> findByOrderStatus(OrderStatus orderStatus);

    /**
     * Find orders by customer
     * @param customer Customer entity
     * @return List of orders
     */
    List<Order> findByCustomer(User customer);

    /**
     * Find orders with amount greater than specified value
     * @param amount Minimum order amount
     * @return List of orders
     */
    List<Order> findByOrderAmountGreaterThan(BigDecimal amount);

    /**
     * Find orders created after specified date
     * @param date Start date
     * @return List of orders
     */
    List<Order> findByOrderDateAfter(Instant date);

    /**
     * Find orders by customer and status
     * @param customer Customer entity
     * @param orderStatus Order status
     * @return List of orders
     */
    List<Order> findByCustomerAndOrderStatus(User customer, OrderStatus orderStatus);

    /**
     * Count orders by status
     * @param orderStatus Order status
     * @return Count of orders
     */
    long countByOrderStatus(OrderStatus orderStatus);
}
