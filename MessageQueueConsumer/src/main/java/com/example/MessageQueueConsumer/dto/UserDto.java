package com.example.MessageQueueConsumer.dto;

import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.entity.User;
import com.example.MessageQueueConsumer.util.AppUtils;
import com.example.MessageQueueConsumer.util.DateUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;

/**
 * DTO for User operations
 */
public class UserDto {

    /**
     * Request DTO for creating a new user
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "UserCreateRequestDto", description = "Request object for creating a new user")
    public static class Request implements Serializable {

        @NotBlank(message = "Username is required")
        private String userName;

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;

        private String firstName;
        private String lastName;
        private String phoneNumber;

        /**
         * Maps Request DTO to User entity
         */
        public static User mapToEntity(Request request) {
            User user = new User();
            user.setUid(AppUtils.generateUid());
            user.setUserName(request.getUserName());
            user.setEmail(request.getEmail());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
            user.setPhoneNumber(request.getPhoneNumber());
            user.setStatus(Status.ACTIVE);
            user.setCreatedAt(Instant.now());
            return user;
        }
    }

    /**
     * Request DTO for updating an existing user
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "UserUpdateRequestDto", description = "Request object for updating an existing user")
    public static class UpdateRequest implements Serializable {

        private String userName;
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private Status status;

        /**
         * Updates entity with the request data
         */
        public static void updateEntity(UpdateRequest request, User user) {
            if (StringUtils.isNotBlank(request.getUserName())) {
                user.setUserName(request.getUserName());
            }
            if (StringUtils.isNotBlank(request.getEmail())) {
                user.setEmail(request.getEmail());
            }
            if (StringUtils.isNotBlank(request.getFirstName())) {
                user.setFirstName(request.getFirstName());
            }
            if (StringUtils.isNotBlank(request.getLastName())) {
                user.setLastName(request.getLastName());
            }
            if (StringUtils.isNotBlank(request.getPhoneNumber())) {
                user.setPhoneNumber(request.getPhoneNumber());
            }
            if (request.getStatus() != null) {
                user.setStatus(request.getStatus());
            }
            user.setUpdatedAt(Instant.now());
        }
    }

    /**
     * Response DTO for user information
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(name = "UserResponseDto", description = "Response object containing user information")
    public static class Response implements Serializable {

        private String uid;
        private String userName;
        private String email;
        private String firstName;
        private String lastName;
        private String phoneNumber;
        private Status status;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT, timezone = "UTC")
        private Instant createdAt;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateUtils.DEFAULT_DATE_TIME_FORMAT, timezone = "UTC")
        private Instant updatedAt;

        /**
         * Maps User entity to Response DTO
         */
        public static Response mapToDto(User user) {
            return Response.builder()
                    .uid(user.getUid())
                    .userName(user.getUserName())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .phoneNumber(user.getPhoneNumber())
                    .status(user.getStatus())
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
        }
    }

}
