package com.example.MessageQueueConsumer.controller;

import com.example.MessageQueueConsumer.dto.Order;
import com.example.MessageQueueConsumer.service.OrderServiceImpl;
import com.example.MessageQueueConsumer.util.MessageUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/orders")
@RabbitListener(queues = MessageUtil.ORDER_QUEUE)
public class OrderController {
    @Autowired
    private OrderServiceImpl orderService;

    @PostMapping("/save")
    @RabbitListener(queues = MessageUtil.ORDER_QUEUE)
    public void saveOrder(@Valid @RequestBody Order order){
        orderService.saveOrder(order);
    }
}
