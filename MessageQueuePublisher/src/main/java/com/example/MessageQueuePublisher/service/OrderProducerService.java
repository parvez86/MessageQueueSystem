package com.example.MessageQueuePublisher.service;


import com.example.MessageQueuePublisher.entity.Order;

public interface OrderProducerService {
    void sendMessage(Order order);
}
