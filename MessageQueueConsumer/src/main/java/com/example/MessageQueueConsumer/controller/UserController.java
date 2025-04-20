package com.example.MessageQueueConsumer.controller;

import com.example.MessageQueueConsumer.dto.UserDto;
import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.service.UserService;
import com.example.MessageQueueConsumer.util.MessageUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {
    private final UserService userService;

    @Operation(summary = "Create a new user")
    @PostMapping
    public ResponseEntity<UserDto.Response> createUser(@Valid @RequestBody UserDto.Request request) {
        log.info("Creating new user: {}", request.getUserName());
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.createUser(request));
    }

    @Operation(summary = "Get user by UID")
    @GetMapping("/{uid}")
    public ResponseEntity<UserDto.Response> getUserByUid(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable String uid) {
        log.info("Fetching user with UID: {}", uid);
        return ResponseEntity.ok(userService.getUserByUid(uid));
    }

    @Operation(summary = "Get all users with pagination")
    @GetMapping
    public ResponseEntity<Page<UserDto.Response>> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination: {}", pageable);
        return ResponseEntity.ok(userService.getAllUsers(pageable));
    }

    @Operation(summary = "Update user")
    @PutMapping("/{uid}")
    public ResponseEntity<UserDto.Response> updateUser(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable String uid,
            @Valid @RequestBody UserDto.UpdateRequest request) {
        log.info("Updating user with UID: {}", uid);
        return ResponseEntity.ok(userService.updateUser(uid, request));
    }

    @Operation(summary = "Delete user")
    @DeleteMapping("/{uid}")
    public ResponseEntity<Void> deleteUser(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable String uid) {
        log.info("Deleting user with UID: {}", uid);
        userService.deleteUser(uid);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get users by status")
    @GetMapping("/status/{status}")
    public ResponseEntity<List<UserDto.Response>> getUsersByStatus(
            @Parameter(description = "User status", required = true)
            @PathVariable Status status) {
        log.info("Fetching users with status: {}", status);
        return ResponseEntity.ok(userService.getUsersByStatus(status));
    }

    @Operation(summary = "Search users by name or email")
    @GetMapping("/search")
    public ResponseEntity<List<UserDto.Response>> searchUsers(
            @Parameter(description = "Search term for name or email")
            @RequestParam String term) {
        log.info("Searching users with term: {}", term);
        return ResponseEntity.ok(userService.searchUsers(term));
    }

    @Operation(summary = "Update user status")
    @PatchMapping("/{uid}/status")
    public ResponseEntity<UserDto.Response> updateUserStatus(
            @Parameter(description = "User unique identifier", required = true)
            @PathVariable String uid,
            @Parameter(description = "New status", required = true)
            @RequestParam Status status) {
        log.info("Updating status of user with UID: {} to {}", uid, status);
        return ResponseEntity.ok(userService.updateUserStatus(uid, status));
    }

    @Operation(summary = "Process user create message from RabbitMQ")
    @RabbitListener(queues = MessageUtil.USER_QUEUE)
    public void processUserCreationMessage(UserDto.Request userDto) {
        log.info("Received user creation message: {}", userDto);
        userService.processUserMessage(userDto);
    }
}
