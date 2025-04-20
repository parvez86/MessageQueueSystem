package com.example.MessageQueueConsumer.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for date and time operations
 */
public class DateUtils {

    /**
     * Default date time format pattern for the application
     */
    public static final String DEFAULT_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
    
    /**
     * Default date formatter
     */
    public static final DateTimeFormatter DEFAULT_FORMATTER = DateTimeFormatter.ofPattern(DEFAULT_DATE_TIME_FORMAT);
    
    /**
     * Format LocalDateTime to string using default pattern
     * @param dateTime The LocalDateTime to format
     * @return Formatted date time string
     */
    public static String format(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DEFAULT_FORMATTER) : null;
    }
    
    /**
     * Parse string to LocalDateTime using default pattern
     * @param dateTimeStr The string to parse
     * @return Parsed LocalDateTime
     */
    public static LocalDateTime parse(String dateTimeStr) {
        return dateTimeStr != null ? LocalDateTime.parse(dateTimeStr, DEFAULT_FORMATTER) : null;
    }
}