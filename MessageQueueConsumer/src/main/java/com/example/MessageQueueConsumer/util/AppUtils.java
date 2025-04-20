package com.example.MessageQueueConsumer.util;

import java.util.UUID;

/**
 * Utility class for common application functions
 */
public class AppUtils {

    /**
     * Generates a unique identifier for entities
     * @return A unique string ID
     */
    public static String generateUid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    public static RuntimeException throwException (String msg) {
        return new RuntimeException(msg);
    }
}