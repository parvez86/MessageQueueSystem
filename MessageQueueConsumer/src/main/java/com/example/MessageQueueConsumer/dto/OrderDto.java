package com.example.MessageQueueConsumer.dto;

import com.example.MessageQueueConsumer.entity.Order;
import com.example.MessageQueueConsumer.entity.OrderStatus;
import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.entity.User;
import com.example.MessageQueueConsumer.util.AppUtils;
import com.example.MessageQueueConsumer.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO for Order operations
 */
public class OrderDto {

    /**
     * Request DTO for creating a new order
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "OrderCreateRequestDto", description = "Request object for creating a new order")
    public static class Request implements Serializable {

        @NotBlank(message = "Order name is required")
        private String orderName;

        @NotNull(message = "Order amount is required")
        private BigDecimal orderAmount;

        private String customerName;

        /**
         * Maps Request DTO to Order entity
         */
        public static Order mapToEntity(Request request) {
            Order order = new Order();
            order.setOrderId(AppUtils.generateUid());
            order.setOrderName(request.getOrderName());
            order.setOrderAmount(request.getOrderAmount());
            order.setOrderDate(Instant.now());
            order.setOrderStatus(OrderStatus.PENDING);
            order.setStatus(Status.ACTIVE);
            order.setCreatedAt(Instant.now());
            return order;
        }

        /**
         * Maps Request DTO to Order entity with customer
         */
        public static Order mapToEntity(Request request, User customer) {
            Order order = new Order();
            order.setOrderId(AppUtils.generateUid());
            order.setOrderName(request.getOrderName());
            order.setOrderAmount(request.getOrderAmount());
            order.setCustomer(customer);
            order.setOrderDate(Instant.now());
            order.setOrderStatus(OrderStatus.PENDING);
            order.setStatus(Status.ACTIVE);
            order.setCreatedAt(Instant.now());
            return order;
        }
    }

    /**
     * Request DTO for updating an existing order
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "OrderUpdateRequestDto", description = "Request object for updating an existing order")
    public static class UpdateRequest implements Serializable {

        private String orderName;
        private BigDecimal orderAmount;
        private OrderStatus orderStatus;
        private Status status;

        /**
         * Updates entity with the request data
         */
        public static void updateEntity(UpdateRequest request, Order order) {
            if (StringUtils.isNotBlank(request.getOrderName())) {
                order.setOrderName(request.getOrderName());
            }

            if (request.getOrderAmount() != null) {
                order.setOrderAmount(request.getOrderAmount());
            }

            if (request.getOrderStatus() != null) {
                order.setOrderStatus(request.getOrderStatus());
            }

            if (request.getStatus() != null) {
                order.setStatus(request.getStatus());
            }
            order.setUpdatedAt(Instant.now());
        }
    }

    /**
     * Response DTO for order information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "OrderResponseDto", description = "Response object containing order information")
    public static class Response implements Serializable {

        private String orderId;
        private String orderName;
        private BigDecimal orderAmount;
        private String customerUid;
        private String customerName;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT, timezone = "UTC")
        private Instant orderDate;

        private OrderStatus orderStatus;
        private Status status;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT, timezone = "UTC")
        private Instant createdAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT, timezone = "UTC")
        private Instant updatedAt;

        /**
         * Maps Order entity to Response DTO
         */
        public static Response mapToDto(Order order) {
            Response.ResponseBuilder builder = Response.builder()
                    .orderId(order.getOrderId())
                    .orderName(order.getOrderName())
                    .orderAmount(order.getOrderAmount())
                    .orderDate(order.getOrderDate())
                    .orderStatus(order.getOrderStatus())
                    .status(order.getStatus())
                    .createdAt(order.getCreatedAt())
                    .updatedAt(order.getUpdatedAt());

            if (order.getCustomer() != null) {
                builder.customerUid(order.getCustomer().getUid())
                        .customerName(order.getCustomer().getUserName());
            }

            return builder.build();
        }
    }
}
