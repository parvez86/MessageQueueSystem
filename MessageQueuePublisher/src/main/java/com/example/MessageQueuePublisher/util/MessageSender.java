package com.example.MessageQueuePublisher.util;

import ch.qos.logback.classic.pattern.MessageConverter;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageSender {
    private static final Logger LOG = LoggerFactory.getLogger(MessageSender.class);
    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitTemplate rabbitTemplate;



    public MessageSender(AmqpAdmin amqpAdmin, RabbitTemplate rabbitTemplate){
        this.amqpAdmin = amqpAdmin;
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    private void initRabbit(){
        this.rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }

    public void sendMessage(String exchange, String routing_key, Object object){
        this.rabbitTemplate.convertAndSend(exchange, routing_key, object);
    }
}
