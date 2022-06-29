package com.example.MessageQueuePublisher.service;

import com.example.MessageQueuePublisher.entity.User;

public interface UserProducerService {
    void sendMessage(User user);
}
