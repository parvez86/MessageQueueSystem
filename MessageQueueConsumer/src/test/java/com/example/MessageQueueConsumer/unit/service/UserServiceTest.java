package com.example.MessageQueueConsumer.unit.service;

import com.example.MessageQueueConsumer.dto.UserDto;
import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.entity.User;
import com.example.MessageQueueConsumer.repository.UserRepository;
import com.example.MessageQueueConsumer.service.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    private UserDto.Request userRequest;
    private UserDto.UpdateRequest updateRequest;
    private User user;

    @BeforeEach
    void setUp() {
        userRequest = UserDto.Request.builder()
                .userName("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("123-456-7890")
                .build();

        updateRequest = UserDto.UpdateRequest.builder()
                .userName("updateduser")
                .email("updated@example.com")
                .build();

        user = new User();
        user.setId(1L);
        user.setUid("test-uid-123");
        user.setUserName("testuser");
        user.setEmail("test@example.com");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setPhoneNumber("123-456-7890");
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(Instant.now());
        user.setUpdatedAt(null);
    }

    @Test
    void createUser_Success() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto.Response response = userService.createUser(userRequest);

        assertNotNull(response);
        assertEquals("test-uid-123", response.getUid());
        assertEquals("testuser", response.getUserName());
        assertEquals("test@example.com", response.getEmail());
        assertEquals(Status.ACTIVE, response.getStatus());

        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUserByUid_Success() {
        when(userRepository.findByUid("test-uid-123")).thenReturn(Optional.of(user));

        UserDto.Response response = userService.getUserByUid("test-uid-123");

        assertNotNull(response);
        assertEquals("test-uid-123", response.getUid());
        assertEquals("testuser", response.getUserName());

        verify(userRepository).findByUid("test-uid-123");
    }

    @Test
    void getUserByUid_NotFound() {
        when(userRepository.findByUid("non-existent-uid")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> userService.getUserByUid("non-existent-uid"));

        verify(userRepository).findByUid("non-existent-uid");
    }

    @Test
    void getAllUsers_Success() {
        List<User> users = Arrays.asList(user);
        Page<User> page = new PageImpl<>(users);
        Pageable pageable = PageRequest.of(0, 10);

        when(userRepository.findAll(pageable)).thenReturn(page);

        Page<UserDto.Response> response = userService.getAllUsers(pageable);

        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals("test-uid-123", response.getContent().get(0).getUid());

        verify(userRepository).findAll(pageable);
    }

    @Test
    void updateUser_Success() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUid("test-uid-123");
        updatedUser.setUserName("updateduser");
        updatedUser.setEmail("updated@example.com");
        updatedUser.setFirstName("Test");
        updatedUser.setLastName("User");
        updatedUser.setStatus(Status.ACTIVE);
        updatedUser.setCreatedAt(Instant.now());
        updatedUser.setUpdatedAt(Instant.now());

        when(userRepository.findByUid("test-uid-123")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto.Response response = userService.updateUser("test-uid-123", updateRequest);

        assertNotNull(response);
        assertEquals("test-uid-123", response.getUid());
        assertEquals("updateduser", response.getUserName());
        assertEquals("updated@example.com", response.getEmail());

        verify(userRepository).findByUid("test-uid-123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void deleteUser_Success() {
        when(userRepository.findByUid("test-uid-123")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        userService.deleteUser("test-uid-123");

        verify(userRepository).findByUid("test-uid-123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void getUsersByStatus_Success() {
        List<User> activeUsers = Arrays.asList(user);
        when(userRepository.findByStatus(Status.ACTIVE)).thenReturn(activeUsers);

        List<UserDto.Response> response = userService.getUsersByStatus(Status.ACTIVE);

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("test-uid-123", response.get(0).getUid());

        verify(userRepository).findByStatus(Status.ACTIVE);
    }

    @Test
    void searchUsers_Success() {
        List<User> searchResults = Arrays.asList(user);
        when(userRepository.findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase("test", "test"))
                .thenReturn(searchResults);

        List<UserDto.Response> response = userService.searchUsers("test");

        assertNotNull(response);
        assertEquals(1, response.size());
        assertEquals("test-uid-123", response.get(0).getUid());

        verify(userRepository).findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase("test", "test");
    }

    @Test
    void updateUserStatus_Success() {
        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setUid("test-uid-123");
        updatedUser.setUserName("testuser");
        updatedUser.setEmail("test@example.com");
        updatedUser.setStatus(Status.INACTIVE);
        updatedUser.setCreatedAt(Instant.now());
        updatedUser.setUpdatedAt(Instant.now());

        when(userRepository.findByUid("test-uid-123")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);

        UserDto.Response response = userService.updateUserStatus("test-uid-123", Status.INACTIVE);

        assertNotNull(response);
        assertEquals("test-uid-123", response.getUid());
        assertEquals(Status.INACTIVE, response.getStatus());

        verify(userRepository).findByUid("test-uid-123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void processUserMessage_Success() {
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto.Response response = userService.processUserMessage(userRequest);

        assertNotNull(response);
        assertEquals("test-uid-123", response.getUid());
        
        verify(userRepository).save(any(User.class));
    }
}