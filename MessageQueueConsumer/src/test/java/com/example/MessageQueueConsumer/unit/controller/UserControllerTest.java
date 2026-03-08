package com.example.MessageQueueConsumer.controller;

import com.example.MessageQueueConsumer.dto.UserDto;
import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Autowired
    private ObjectMapper objectMapper;

    private UserDto.Request userRequest;
    private UserDto.Response userResponse;

    @BeforeEach
    void setUp() {
        userRequest = UserDto.Request.builder()
                .userName("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("123-456-7890")
                .build();

        userResponse = UserDto.Response.builder()
                .uid("test-uid-123")
                .userName("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .phoneNumber("123-456-7890")
                .status(Status.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
    }

    @Test
    void createUser_Success() throws Exception {
        when(userService.createUser(any(UserDto.Request.class))).thenReturn(userResponse);

        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uid", is("test-uid-123")))
                .andExpect(jsonPath("$.userName", is("testuser")))
                .andExpect(jsonPath("$.email", is("test@example.com")));

        verify(userService).createUser(any(UserDto.Request.class));
    }

    @Test
    void getUserByUid_Success() throws Exception {
        when(userService.getUserByUid("test-uid-123")).thenReturn(userResponse);

        mockMvc.perform(get("/users/test-uid-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", is("test-uid-123")))
                .andExpect(jsonPath("$.userName", is("testuser")));

        verify(userService).getUserByUid("test-uid-123");
    }

    @Test
    void getAllUsers_Success() throws Exception {
        List<UserDto.Response> users = Arrays.asList(userResponse);
        Page<UserDto.Response> page = new PageImpl<>(users);
        
        when(userService.getAllUsers(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].uid", is("test-uid-123")));

        verify(userService).getAllUsers(any(Pageable.class));
    }

    @Test
    void updateUser_Success() throws Exception {
        UserDto.UpdateRequest updateRequest = UserDto.UpdateRequest.builder()
                .userName("updateduser")
                .build();

        UserDto.Response updatedResponse = UserDto.Response.builder()
                .uid("test-uid-123")
                .userName("updateduser")
                .email("test@example.com")
                .status(Status.ACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(userService.updateUser(eq("test-uid-123"), any(UserDto.UpdateRequest.class)))
                .thenReturn(updatedResponse);

        mockMvc.perform(put("/users/test-uid-123")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", is("test-uid-123")))
                .andExpect(jsonPath("$.userName", is("updateduser")));

        verify(userService).updateUser(eq("test-uid-123"), any(UserDto.UpdateRequest.class));
    }

    @Test
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser("test-uid-123");

        mockMvc.perform(delete("/users/test-uid-123"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser("test-uid-123");
    }

    @Test
    void getUsersByStatus_Success() throws Exception {
        List<UserDto.Response> activeUsers = Arrays.asList(userResponse);
        when(userService.getUsersByStatus(Status.ACTIVE)).thenReturn(activeUsers);

        mockMvc.perform(get("/users/status/ACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uid", is("test-uid-123")));

        verify(userService).getUsersByStatus(Status.ACTIVE);
    }

    @Test
    void searchUsers_Success() throws Exception {
        List<UserDto.Response> searchResults = Arrays.asList(userResponse);
        when(userService.searchUsers("test")).thenReturn(searchResults);

        mockMvc.perform(get("/users/search?term=test"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].uid", is("test-uid-123")));

        verify(userService).searchUsers("test");
    }

    @Test
    void updateUserStatus_Success() throws Exception {
        UserDto.Response updatedResponse = UserDto.Response.builder()
                .uid("test-uid-123")
                .userName("testuser")
                .email("test@example.com")
                .status(Status.INACTIVE)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();

        when(userService.updateUserStatus("test-uid-123", Status.INACTIVE)).thenReturn(updatedResponse);

        mockMvc.perform(patch("/users/test-uid-123/status")
                .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", is("test-uid-123")))
                .andExpect(jsonPath("$.status", is("INACTIVE")));

        verify(userService).updateUserStatus("test-uid-123", Status.INACTIVE);
    }

    @Test
    void processUserCreationMessage_Success() throws Exception {
        // Testing RabbitMQ listener method
        doNothing().when(userService).processUserMessage(any(UserDto.Request.class));

        // Direct method invocation since it's not a REST endpoint
        UserController controller = new UserController(userService);
        controller.processUserCreationMessage(userRequest);

        verify(userService).processUserMessage(any(UserDto.Request.class));
    }
}