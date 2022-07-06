package com.example.MessageQueueConsumer.service;

import com.example.MessageQueueConsumer.dto.UserDto;

public interface UserService {
    void saveUser(UserDto userDto);
}
