package com.example.MessageQueuePublisher.dto;

import lombok.*;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserDto implements Serializable {
    private String userId;
    private String userName;
}
