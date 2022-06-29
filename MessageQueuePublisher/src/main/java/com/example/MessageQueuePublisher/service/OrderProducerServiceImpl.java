package com.example.MessageQueuePublisher.service;

import com.example.MessageQueuePublisher.entity.Order;
import com.example.MessageQueuePublisher.entity.User;
import com.example.MessageQueuePublisher.util.MessageSender;
import com.example.MessageQueuePublisher.util.MessageUtil;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Configuration
@Service
public class OrderProducerServiceImpl implements OrderProducerService {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private MessageSender messageSender;


    @Autowired
    private FanoutExchange fanoutExchange;

    private String exchange = MessageUtil.FANOUT_EXCHANGE_ORDER;

    private String routing_key =MessageUtil.ORDER_ROUTING_KEY ;

    private String queue_name = MessageUtil.ORDER_QUEUE;


    @PostConstruct
    public void rabbitmqInit(){
        amqpAdmin = new RabbitAdmin(rabbitTemplate);
        amqpAdmin.declareQueue(new Queue(queue_name));
        amqpAdmin.declareExchange(new FanoutExchange(exchange, true, false, null));
        amqpAdmin.declareBinding(new Binding(queue_name, Binding.DestinationType.QUEUE, exchange, "", null));
    }

    public void sendMessage(Order order) {
        rabbitmqInit();
        messageSender = new MessageSender(amqpAdmin, rabbitTemplate);
        messageSender.sendMessage(exchange, "", order);
    }
}
