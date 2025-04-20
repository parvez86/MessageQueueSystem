package com.example.MessageQueueConsumer.service;

import com.example.MessageQueueConsumer.dto.UserDto;
import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.entity.User;
import com.example.MessageQueueConsumer.repository.UserRepository;
import com.example.MessageQueueConsumer.util.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto.Response createUser(UserDto.Request request) {
        log.info("Creating new user: {}", request);
        User user = UserDto.Request.mapToEntity(request);
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getUid());
        return UserDto.Response.mapToDto(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto.Response getUserByUid(String uid) {
        log.info("Fetching user with UID: {}", uid);
        User user = findUserByUid(uid);
        return UserDto.Response.mapToDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserDto.Response> getAllUsers(Pageable pageable) {
        log.info("Fetching all users with pagination: {}", pageable);
        return userRepository.findAll(pageable)
                .map(UserDto.Response::mapToDto);
    }

    @Override
    @Transactional
    public UserDto.Response updateUser(String uid, UserDto.UpdateRequest request) {
        log.info("Updating user with UID: {}", uid);
        User user = findUserByUid(uid);
        UserDto.UpdateRequest.updateEntity(request, user);
        User updatedUser = userRepository.save(user);
        log.info("User updated successfully: {}", uid);
        return UserDto.Response.mapToDto(updatedUser);
    }

    @Override
    @Transactional
    public void deleteUser(String uid) {
        log.info("Deleting user with UID: {}", uid);
        User user = findUserByUid(uid);
        user.softDelete();
        userRepository.save(user);
        log.info("User soft deleted successfully: {}", uid);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto.Response> getUsersByStatus(Status status) {
        log.info("Fetching users with status: {}", status);
        List<User> users = userRepository.findByStatus(status);
        return users.stream()
                .map(UserDto.Response::mapToDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto.Response> searchUsers(String term) {
        log.info("Searching users with term: {}", term);
        List<User> users = userRepository.findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(term, term);
        return users.stream()
                .map(UserDto.Response::mapToDto)
                .toList();
    }

    @Override
    @Transactional
    public UserDto.Response updateUserStatus(String uid, Status status) {
        log.info("Updating status of user with UID: {} to {}", uid, status);
        User user = findUserByUid(uid);
        user.setStatus(status);
        user.setUpdatedAt(Instant.now());
        User updatedUser = userRepository.save(user);
        log.info("User status updated successfully: {}", uid);
        return UserDto.Response.mapToDto(updatedUser);
    }

    @Override
    @Transactional
    public UserDto.Response processUserMessage(UserDto.Request userDto) {
        log.info("Processing user message: {}", userDto);
        return createUser(userDto);
    }

    private User findUserByUid(String uid) {
        return userRepository.findByUid(uid)
                .orElseThrow(() -> AppUtils.throwException("User not found with UID: " + uid));
    }
}
