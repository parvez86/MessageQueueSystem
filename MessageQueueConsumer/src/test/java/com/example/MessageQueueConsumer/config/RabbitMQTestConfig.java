package com.example.MessageQueueConsumer.config;

import com.example.MessageQueueConsumer.dto.OrderDto;
import com.example.MessageQueueConsumer.dto.UserDto;
import com.example.MessageQueueConsumer.service.OrderService;
import com.example.MessageQueueConsumer.service.UserService;
import com.example.MessageQueueConsumer.util.MessageUtil;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.amqp.rabbit.listener.MessageListenerContainer;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;

/**
 * RabbitMQ test configuration for integration testing
 */
@TestConfiguration
@EnableRabbit
@Profile("test")
public class RabbitMQTestConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    @Profile("test-with-mq")
    public MessageListenerContainer userMessageListenerContainer(
            ConnectionFactory connectionFactory,
            UserService userService) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(MessageUtil.USER_QUEUE);
        container.setMessageListener(message -> {
            try {
                UserDto.Request userRequest = (UserDto.Request) messageConverter().fromMessage(message);
                userService.processUserMessage(userRequest);
            } catch (Exception e) {
                throw new RuntimeException("Error processing user message", e);
            }
        });
        return container;
    }

    @Bean
    @Profile("test-with-mq")
    public MessageListenerContainer orderMessageListenerContainer(
            ConnectionFactory connectionFactory,
            OrderService orderService) {

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(MessageUtil.ORDER_QUEUE);
        container.setMessageListener(message -> {
            try {
                OrderDto.Request orderRequest = (OrderDto.Request) messageConverter().fromMessage(message);
                orderService.processOrderMessage(orderRequest);
            } catch (Exception e) {
                throw new RuntimeException("Error processing order message", e);
            }
        });
        return container;
    }
}