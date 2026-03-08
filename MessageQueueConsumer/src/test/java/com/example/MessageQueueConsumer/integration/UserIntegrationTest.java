package com.example.MessageQueueConsumer.integration;

import com.example.MessageQueueConsumer.dto.UserDto;
import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    private String createdUserUid;

    @BeforeEach
    void setUp() throws Exception {
        userRepository.deleteAll();
        
        // Create a test user
        UserDto.Request userRequest = UserDto.Request.builder()
                .userName("integrationtestuser")
                .email("integration@example.com")
                .firstName("Integration")
                .lastName("Test")
                .phoneNumber("123-456-7890")
                .build();
                
        MvcResult userResult = mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andReturn();
                
        UserDto.Response userResponse = objectMapper.readValue(
                userResult.getResponse().getContentAsString(), 
                UserDto.Response.class);
                
        createdUserUid = userResponse.getUid();
        assertNotNull(createdUserUid);
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @Test
    void createUser_Success() throws Exception {
        UserDto.Request newUserRequest = UserDto.Request.builder()
                .userName("newintegrationuser")
                .email("newintegration@example.com")
                .firstName("New")
                .lastName("Integration")
                .phoneNumber("987-654-3210")
                .build();
                
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUserRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.uid", notNullValue()))
                .andExpect(jsonPath("$.userName", is("newintegrationuser")))
                .andExpect(jsonPath("$.email", is("newintegration@example.com")))
                .andExpect(jsonPath("$.firstName", is("New")))
                .andExpect(jsonPath("$.lastName", is("Integration")))
                .andExpect(jsonPath("$.status", is("ACTIVE")));
    }

    @Test
    void getUserByUid_Success() throws Exception {
        mockMvc.perform(get("/users/{uid}", createdUserUid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", is(createdUserUid)))
                .andExpect(jsonPath("$.userName", is("integrationtestuser")))
                .andExpect(jsonPath("$.email", is("integration@example.com")))
                .andExpect(jsonPath("$.firstName", is("Integration")))
                .andExpect(jsonPath("$.lastName", is("Test")));
    }
    
    @Test
    void getUserByUid_NotFound() throws Exception {
        mockMvc.perform(get("/users/{uid}", "non-existent-uid"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_Success() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.content[*].uid", hasItem(createdUserUid)));
    }

    @Test
    void updateUser_Success() throws Exception {
        UserDto.UpdateRequest updateRequest = UserDto.UpdateRequest.builder()
                .userName("updatedintegrationuser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("User")
                .build();

        mockMvc.perform(put("/users/{uid}", createdUserUid)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", is(createdUserUid)))
                .andExpect(jsonPath("$.userName", is("updatedintegrationuser")))
                .andExpect(jsonPath("$.email", is("updated@example.com")))
                .andExpect(jsonPath("$.firstName", is("Updated")))
                .andExpect(jsonPath("$.lastName", is("User")));
                
        // Verify the changes were persisted
        mockMvc.perform(get("/users/{uid}", createdUserUid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName", is("updatedintegrationuser")))
                .andExpect(jsonPath("$.email", is("updated@example.com")));
    }

    @Test
    void getUsersByStatus_ReturnsCorrectUsers() throws Exception {
        mockMvc.perform(get("/users/status/{status}", Status.ACTIVE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].uid", hasItem(createdUserUid)))
                .andExpect(jsonPath("$[*].status", everyItem(is("ACTIVE"))));
    }

    @Test
    void searchUsers_ReturnsMatchingUsers() throws Exception {
        mockMvc.perform(get("/users/search").param("term", "integration"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].uid", hasItem(createdUserUid)));
                
        // Search by email
        mockMvc.perform(get("/users/search").param("term", "integration@example"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[*].uid", hasItem(createdUserUid)));
                
        // Search that should return no results
        mockMvc.perform(get("/users/search").param("term", "nonexistent"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void updateUserStatus_Success() throws Exception {
        mockMvc.perform(patch("/users/{uid}/status", createdUserUid)
                .param("status", "INACTIVE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uid", is(createdUserUid)))
                .andExpect(jsonPath("$.status", is("INACTIVE")));
                
        // Verify the status change was persisted
        mockMvc.perform(get("/users/{uid}", createdUserUid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("INACTIVE")));
    }
    
    @Test
    void deleteUser_Success() throws Exception {
        mockMvc.perform(delete("/users/{uid}", createdUserUid))
                .andExpect(status().isNoContent());
                
        // Verify the user is now deleted (soft delete)
        mockMvc.perform(get("/users/{uid}", createdUserUid))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("DELETED")));
    }
    
    @Test
    void createUser_ValidationFailure() throws Exception {
        UserDto.Request invalidUserRequest = UserDto.Request.builder()
                .userName("")  // Empty username should fail validation
                .email("invalid-email")  // Invalid email format
                .build();
                
        mockMvc.perform(post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidUserRequest)))
                .andExpect(status().isBadRequest());
    }
}