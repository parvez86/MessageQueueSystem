package com.example.MessageQueuePublisher.controller;


import com.example.MessageQueuePublisher.dto.UserDto;
import com.example.MessageQueuePublisher.service.UserProducerServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserProducerController {
    private final UserProducerServiceImpl userProducerService;

    @PostMapping
    public ResponseEntity<String> sendMessage(@Valid @RequestBody UserDto userDto) {
        userProducerService.sendMessage(userDto);
        log.info("user sent: " + userDto);
        return ResponseEntity.ok("Message is successfully sent");
    }
}
