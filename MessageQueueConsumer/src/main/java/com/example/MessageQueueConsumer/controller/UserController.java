package com.example.MessageQueueConsumer.controller;

import com.example.MessageQueueConsumer.dto.User;
import com.example.MessageQueueConsumer.service.UserServiceImpl;
import com.example.MessageQueueConsumer.util.MessageUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserServiceImpl userService;

    @PostMapping("/save")
    @RabbitListener(queues = MessageUtil.USER_QUEUE)
    public void saveUser(@Valid @RequestBody User user){
        userService.saveUser(user);
    }
}
