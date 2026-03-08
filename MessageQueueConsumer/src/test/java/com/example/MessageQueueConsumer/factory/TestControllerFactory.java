package com.example.MessageQueueConsumer.factory;

import com.example.MessageQueueConsumer.controller.OrderController;
import com.example.MessageQueueConsumer.controller.UserController;
import com.example.MessageQueueConsumer.service.OrderService;
import com.example.MessageQueueConsumer.service.OrderServiceImpl;
import com.example.MessageQueueConsumer.service.UserService;
import com.example.MessageQueueConsumer.service.UserServiceImpl;
import org.mockito.Mockito;

/**
 * Factory class for creating test controllers with mocked dependencies
 */
public class TestControllerFactory {

    /**
     * Creates a UserController with mocked dependencies
     *
     * @return A UserController with mocked UserService
     */
    public static UserController createUserController() {
        UserService userService = Mockito.mock(UserServiceImpl.class);
        return new UserController(userService);
    }

    /**
     * Creates a UserController with the provided UserService
     *
     * @param userService The UserService implementation to use
     * @return A UserController with the specified UserService
     */
    public static UserController createUserController(UserService userService) {
        return new UserController(userService);
    }

    /**
     * Creates an OrderController with mocked dependencies
     *
     * @return An OrderController with mocked OrderService
     */
    public static OrderController createOrderController() {
        OrderService orderService = Mockito.mock(OrderServiceImpl.class);
        return new OrderController(orderService);
    }

    /**
     * Creates an OrderController with the provided OrderService
     *
     * @param orderService The OrderService implementation to use
     * @return An OrderController with the specified OrderService
     */
    public static OrderController createOrderController(OrderService orderService) {
        return new OrderController(orderService);
    }
}