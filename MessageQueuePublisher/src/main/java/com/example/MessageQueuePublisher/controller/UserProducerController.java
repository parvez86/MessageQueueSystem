package com.example.MessageQueuePublisher.controller;


import com.example.MessageQueuePublisher.entity.User;
import com.example.MessageQueuePublisher.service.UserProducerServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserProducerController {
    @Autowired
    private UserProducerServiceImpl userProducerService;
    private static final Logger logger = LoggerFactory.getLogger(UserProducerController.class);

    @Value("${app.message}")
    private String response;

    @PostMapping("/produce")
    public ResponseEntity<String> sendMessage(@RequestBody User user) {
        userProducerService.sendMessage(user);
        logger.info("user sent: " + user);
        return ResponseEntity.ok(response);
    }
}
