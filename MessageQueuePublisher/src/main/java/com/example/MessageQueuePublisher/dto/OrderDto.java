package com.example.MessageQueuePublisher.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderDto {
    @NotBlank(message = "Order name is required")
    private String orderName;

    @NotNull(message = "Order amount is required")
    private BigDecimal orderAmount;

    @NotBlank(message = "Username is required")
    private String customerName;
}
