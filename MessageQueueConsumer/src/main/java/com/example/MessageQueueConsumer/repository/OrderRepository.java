package com.example.MessageQueueConsumer.repository;
import com.example.MessageQueueConsumer.dto.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
