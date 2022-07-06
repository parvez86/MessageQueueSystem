package com.example.MessageQueuePublisher.controller;

import com.example.MessageQueuePublisher.dto.OrderDto;
import com.example.MessageQueuePublisher.service.OrderProducerServiceImpl;
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
@RequestMapping("/api/orders/")
public class OrderProducerController {
    @Autowired
    private OrderProducerServiceImpl orderProducerService;
    private static final Logger logger = LoggerFactory.getLogger(UserProducerController.class);

    @Value("${app.message}")
    private String response;

    @PostMapping("/produce")
    public ResponseEntity<String> sendMessage(@RequestBody OrderDto orderDto) {
        orderProducerService.sendMessage(orderDto);
        logger.info("Order sent: " + orderDto);
        return ResponseEntity.ok(response);
    }
}
