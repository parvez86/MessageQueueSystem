package com.example.MessageQueueConsumer.service;

import com.example.MessageQueueConsumer.dto.OrderDto;
import com.example.MessageQueueConsumer.entity.Order;
import com.example.MessageQueueConsumer.repository.OrderRepository;
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
    public void saveOrder(OrderDto orderDto) {
        if(Objects.nonNull(orderDto)) {
            Order order = new Order();
            order.setOrderId(orderDto.getOrderId());
            order.setOrderName(orderDto.getOrderName());
            orderRepository.save(order);
        }
        System.out.println("Order is successfully saved...");
        System.out.println(orderDto);
    }
}
