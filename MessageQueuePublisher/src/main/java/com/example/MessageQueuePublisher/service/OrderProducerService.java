package com.example.MessageQueuePublisher.service;


import com.example.MessageQueuePublisher.dto.OrderDto;

public interface OrderProducerService {
    void sendMessage(OrderDto orderDto);
}
