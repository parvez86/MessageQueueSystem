package com.example.MessageQueueConsumer.config;
import com.example.MessageQueueConsumer.dto.User;
import com.example.MessageQueueConsumer.service.UserServiceImpl;
import com.example.MessageQueueConsumer.util.MessageSender;
import com.example.MessageQueueConsumer.util.MessageUtil;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class MessageHandler {
    @Autowired
    private UserServiceImpl userService;

//    @RabbitListener(queues = MessageUtil.USER_QUEUE)
    public void receivedMessage(User user){
        userService.saveUser(user);
    }
}
