package com.example.MessageQueueConsumer.unit.repository;

import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.entity.User;
import com.example.MessageQueueConsumer.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void findByUid_Success() {
        // Arrange
        User user = new User();
        user.setUid("test-uid-123");
        user.setUserName("testuser");
        user.setEmail("test@example.com");
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(Instant.now());
        
        entityManager.persist(user);
        entityManager.flush();

        // Act
        Optional<User> foundUser = userRepository.findByUid("test-uid-123");

        // Assert
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUserName());
        assertEquals("test@example.com", foundUser.get().getEmail());
    }

    @Test
    void findByUid_NotFound_ReturnsEmpty() {
        // Act
        Optional<User> foundUser = userRepository.findByUid("non-existent-uid");

        // Assert
        assertFalse(foundUser.isPresent());
    }

    @Test
    void findByStatus_ReturnsCorrectUsers() {
        // Arrange
        User activeUser = new User();
        activeUser.setUid("active-uid-123");
        activeUser.setUserName("activeuser");
        activeUser.setEmail("active@example.com");
        activeUser.setStatus(Status.ACTIVE);
        activeUser.setCreatedAt(Instant.now());
        
        User inactiveUser = new User();
        inactiveUser.setUid("inactive-uid-456");
        inactiveUser.setUserName("inactiveuser");
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setStatus(Status.INACTIVE);
        inactiveUser.setCreatedAt(Instant.now());
        
        entityManager.persist(activeUser);
        entityManager.persist(inactiveUser);
        entityManager.flush();

        // Act
        List<User> activeUsers = userRepository.findByStatus(Status.ACTIVE);
        List<User> inactiveUsers = userRepository.findByStatus(Status.INACTIVE);
        List<User> deletedUsers = userRepository.findByStatus(Status.DELETED);

        // Assert
        assertTrue(activeUsers.size() >= 1);
        assertTrue(activeUsers.stream().anyMatch(u -> "activeuser".equals(u.getUserName())));
        
        assertTrue(inactiveUsers.size() >= 1);
        assertTrue(inactiveUsers.stream().anyMatch(u -> "inactiveuser".equals(u.getUserName())));
        
        assertEquals(0, deletedUsers.size());
    }

    @Test
    void findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase_ReturnsMatchingUsers() {
        // Arrange
        User user1 = new User();
        user1.setUid("uid-123");
        user1.setUserName("testuser");
        user1.setEmail("test@example.com");
        user1.setStatus(Status.ACTIVE);
        user1.setCreatedAt(Instant.now());
        
        User user2 = new User();
        user2.setUid("uid-456");
        user2.setUserName("otheruser");
        user2.setEmail("other@example.com");
        user2.setStatus(Status.ACTIVE);
        user2.setCreatedAt(Instant.now());
        
        entityManager.persist(user1);
        entityManager.persist(user2);
        entityManager.flush();

        // Act
        List<User> searchByName = userRepository
                .findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase("test", "test");
        
        List<User> searchByEmail = userRepository
                .findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase("other", "other");
                
        List<User> searchMixed = userRepository
                .findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase("user", "user");
                
        List<User> searchNonExistent = userRepository
                .findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase("nonexistent", "nonexistent");

        // Assert
        assertEquals(1, searchByName.size());
        assertEquals("testuser", searchByName.get(0).getUserName());
        
        assertEquals(1, searchByEmail.size());
        assertEquals("otheruser", searchByEmail.get(0).getUserName());
        
        assertEquals(0, searchNonExistent.size());
    }
    
    @Test
    void existsByUserName_ReturnsCorrectResult() {
        // Arrange
        User user = new User();
        user.setUid("test-uid-123");
        user.setUserName("existinguser");
        user.setEmail("existing@example.com");
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(Instant.now());
        
        entityManager.persist(user);
        entityManager.flush();

        // Act
        boolean exists = userRepository.existsByUserName("existinguser");
        boolean notExists = userRepository.existsByUserName("nonexistinguser");

        // Assert
        assertTrue(exists);
        assertFalse(notExists);
    }
    
    @Test
    void existsByEmail_ReturnsCorrectResult() {
        // Arrange
        User user = new User();
        user.setUid("test-uid-123");
        user.setUserName("emailuser");
        user.setEmail("existing@example.com");
        user.setStatus(Status.ACTIVE);
        user.setCreatedAt(Instant.now());
        
        entityManager.persist(user);
        entityManager.flush();

        // Act
        boolean exists = userRepository.existsByEmail("existing@example.com");
        boolean notExists = userRepository.existsByEmail("nonexisting@example.com");

        // Assert
        assertTrue(exists);
        assertFalse(notExists);
    }
}