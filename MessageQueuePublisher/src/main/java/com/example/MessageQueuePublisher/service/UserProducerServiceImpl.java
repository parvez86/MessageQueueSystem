package com.example.MessageQueuePublisher.service;

import com.example.MessageQueuePublisher.entity.User;
import com.example.MessageQueuePublisher.util.MessageSender;
import com.example.MessageQueuePublisher.util.MessageUtil;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class UserProducerServiceImpl implements UserProducerService{
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private MessageSender messageSender;

    private String exchange = MessageUtil.DIRECT_EXCHANGE_USER;

    private String routing_key =MessageUtil.USER_ROUTING_KEY ;

    private String queue = MessageUtil.USER_QUEUE;


    @PostConstruct
    public void rabbitInit(){
        amqpAdmin = new RabbitAdmin(rabbitTemplate);
        amqpAdmin.declareQueue( new Queue(queue));
        amqpAdmin.declareExchange(new DirectExchange(exchange, true, false));
        amqpAdmin.declareBinding(new Binding(queue, Binding.DestinationType.QUEUE, exchange, routing_key, null));

    }

    public void sendMessage(User user) {
        rabbitInit();
        messageSender = new MessageSender(amqpAdmin, rabbitTemplate);
        messageSender.sendMessage(exchange, routing_key, user);
    }
}
