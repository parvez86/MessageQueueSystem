package com.example.MessageQueueConsumer.service;

import com.example.MessageQueueConsumer.dto.User;
import com.example.MessageQueueConsumer.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    ObjectMapper objectMapper = new ObjectMapper();
    public void saveUser(User user) {
        if(Objects.nonNull(user)) userRepository.save(user);
        System.out.println("User is successfully saved...");
        System.out.println(user);
    }
}
