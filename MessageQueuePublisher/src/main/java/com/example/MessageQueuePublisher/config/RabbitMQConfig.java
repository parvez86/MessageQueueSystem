package com.example.MessageQueuePublisher.config;

import com.example.MessageQueuePublisher.util.MessageUtil;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${spring.rabbitmq.username}")
    private String username;

    @Value("${spring.rabbitmq.password}")
    private String password;

    @Value("${spring.rabbitmq.host}")
    private String host;

    private String user_exchange = MessageUtil.DIRECT_EXCHANGE_USER;
    private String order_exchange = MessageUtil.FANOUT_EXCHANGE_ORDER;

//    @Bean
//    public Queue queue(){
//        return new Queue("", true);
//    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(user_exchange, true, false, null);
    }

    @Bean
    public FanoutExchange fanoutExchange() {
        return new FanoutExchange(order_exchange, true, false, null);
    }

    @Bean(name = "admin_connection_factory")
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory cachingConnectionFactory = new CachingConnectionFactory(host);
        cachingConnectionFactory.setUsername(username);
        cachingConnectionFactory.setPassword(password);
        return cachingConnectionFactory;
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(jsonMessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public AmqpAdmin amqpAdmin(@Qualifier("admin_connection_factory") ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
