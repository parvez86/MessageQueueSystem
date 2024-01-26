package com.example.MessageQueueConsumer.service;

import com.example.MessageQueueConsumer.dto.UserDto;
import com.example.MessageQueueConsumer.entity.Status;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {
    /**
     * Create a new user
     * @param request User creation request
     * @return Created user response
     */
    UserDto.Response createUser(UserDto.Request request);

    /**
     * Get user by UID
     * @param uid User unique identifier
     * @return User response
     */
    UserDto.Response getUserByUid(String uid);

    /**
     * Get all users with pagination
     * @param pageable Pagination information
     * @return Page of user responses
     */
    Page<UserDto.Response> getAllUsers(Pageable pageable);

    /**
     * Update user information
     * @param uid User unique identifier
     * @param request User update request
     * @return Updated user response
     */
    UserDto.Response updateUser(String uid, UserDto.UpdateRequest request);

    /**
     * Delete user by UID
     * @param uid User unique identifier
     */
    void deleteUser(String uid);

    /**
     * Get users by status
     * @param status User status
     * @return List of user responses
     */
    List<UserDto.Response> getUsersByStatus(Status status);

    /**
     * Search users by name or email
     * @param term Search term
     * @return List of user responses
     */
    List<UserDto.Response> searchUsers(String term);

    /**
     * Update user status
     * @param uid User unique identifier
     * @param status New status
     * @return Updated user response
     */
    UserDto.Response updateUserStatus(String uid, Status status);

    /**
     * Process user from message queue
     * @param userDto User DTO from message
     * @return Processed user response
     */
    UserDto.Response processUserMessage(UserDto.Request userDto);
}
