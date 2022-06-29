package com.example.MessageQueuePublisher.entity;

import lombok.*;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class User implements Serializable {
    private String userId;
    private String userName;
}
