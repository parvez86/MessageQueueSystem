package com.example.MessageQueuePublisher.util;

public class MessageUtil {
    public static final String USER_QUEUE = "user.queue";
    public static final String ORDER_QUEUE = "order.queue";

    public static final String DIRECT_EXCHANGE_USER = "user.direct.exchange";
    public static final String FANOUT_EXCHANGE_ORDER = "order.fanout.exchange";

    public static final String USER_ROUTING_KEY = "user_routing_key";
    public static final String ORDER_ROUTING_KEY = "order_routing_key";
}

