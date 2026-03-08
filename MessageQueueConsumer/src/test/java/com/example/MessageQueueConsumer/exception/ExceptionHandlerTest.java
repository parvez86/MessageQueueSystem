package com.example.MessageQueueConsumer.exception;

import com.example.MessageQueueConsumer.controller.UserController;
import com.example.MessageQueueConsumer.dto.UserDto;
import com.example.MessageQueueConsumer.service.UserService;
import com.example.MessageQueueConsumer.util.AppUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for the global exception handler
 */
@WebMvcTest(UserController.class)
@ExtendWith(MockitoExtension.class)
public class ExceptionHandlerTest {

    @Mock
    private MockMvc mockMvc;

    @Mock
    private ObjectMapper objectMapper;

    @InjectMocks
    private UserService userService;

    @Test
    void handleResourceNotFoundException() throws Exception {
        // Setup
        when(userService.getUserByUid(anyString()))
                .thenThrow(AppUtils.throwException("User not found with ID: test-id"));

        // Execute and Verify
        mockMvc.perform(get("/users/test-id"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("User not found with ID: test-id"));
    }

    @Test
    void handleValidationException() throws Exception {
        // Setup - send an invalid request with empty required fields
        UserDto.Request invalidRequest = UserDto.Request.builder()
                .userName("")
                .email("invalid-email")
                .build();

        // Execute and Verify
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").isArray())
                .andExpect(jsonPath("$.errors").isNotEmpty());
    }

    @Test
    void handleGenericException() throws Exception {
        // Setup
        when(userService.getUserByUid(anyString()))
                .thenThrow(AppUtils.throwException("Unexpected error"));

        // Execute and Verify
        mockMvc.perform(get("/users/test-id"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Unexpected error"));
    }
}