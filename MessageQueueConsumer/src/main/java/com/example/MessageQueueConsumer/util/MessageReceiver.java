package com.example.MessageQueueConsumer.util;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MessageReceiver {
    private static final Logger LOG = LoggerFactory.getLogger(MessageReceiver.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public MessageReceiver(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
    }

    @PostConstruct
    private void initRabbit(){
        this.rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
    }

}
