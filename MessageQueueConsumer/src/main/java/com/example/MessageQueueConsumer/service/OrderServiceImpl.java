package com.example.MessageQueueConsumer.service;

import com.example.MessageQueueConsumer.dto.Order;
import com.example.MessageQueueConsumer.dto.User;
import com.example.MessageQueueConsumer.repository.OrderRepository;
import com.example.MessageQueueConsumer.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService{
    @Autowired
    private OrderRepository orderRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    ObjectMapper objectMapper = new ObjectMapper();
    public void saveOrder(Order order) {
        if(Objects.nonNull(order)) orderRepository.save(order);
        System.out.println("Order is successfully saved...");
        System.out.println(order);
    }
}
