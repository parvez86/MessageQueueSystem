package com.example.MessageQueueConsumer.config;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import org.mockito.Mockito;

/**
 * Configuration class for test environment
 */
@TestConfiguration
@Profile("test")
public class TestConfig {

    /**
     * Mocks RabbitTemplate for tests to avoid actual messaging
     */
    @Bean
    @Primary
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate mockRabbitTemplate = Mockito.mock(RabbitTemplate.class);
        return mockRabbitTemplate;
    }
    
    /**
     * Creates a message converter for tests
     */
    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}