package com.example.MessageQueuePublisher.service;

import com.example.MessageQueuePublisher.dto.UserDto;

public interface UserProducerService {
    void sendMessage(UserDto userDto);
}
