package com.example.MessageQueuePublisher.controller;

import com.example.MessageQueuePublisher.dto.OrderDto;
import com.example.MessageQueuePublisher.service.OrderProducerServiceImpl;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders/")
@RequiredArgsConstructor
@Slf4j
@Validated
public class OrderProducerController {
    private final OrderProducerServiceImpl orderProducerService;
    @PostMapping("/produce")
    public ResponseEntity<String> sendMessage(@Valid @RequestBody OrderDto orderDto) {
        orderProducerService.sendMessage(orderDto);
        log.info("Order sent: " + orderDto);
        return ResponseEntity.ok("Message is successfully sent");
    }
}
