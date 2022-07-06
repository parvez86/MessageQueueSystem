package com.example.MessageQueueConsumer.service;

import com.example.MessageQueueConsumer.dto.OrderDto;

public interface OrderService {
    void saveOrder(OrderDto orderDto);
}
