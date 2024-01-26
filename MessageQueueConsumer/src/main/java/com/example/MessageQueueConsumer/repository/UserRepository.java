package com.example.MessageQueueConsumer.repository;

import com.example.MessageQueueConsumer.entity.Status;
import com.example.MessageQueueConsumer.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * Find user by unique identifier
     * @param uid User unique identifier
     * @return Optional of User
     */
    Optional<User> findByUid(String uid);

    /**
     * Find user by username
     * @param userName Username
     * @return Optional of User
     */
    Optional<User> findByUserName(String userName);

    /**
     * Find user by email
     * @param email Email
     * @return Optional of User
     */
    Optional<User> findByEmail(String email);

    /**
     * Find users by status
     * @param status User status
     * @return List of users
     */
    List<User> findByStatus(Status status);

    /**
     * Search users by username or email
     * @param userName Username search term
     * @param email Email search term
     * @return List of users
     */
    List<User> findByUserNameContainingIgnoreCaseOrEmailContainingIgnoreCase(String userName, String email);

    /**
     * Check if user exists by username
     * @param userName Username
     * @return True if exists
     */
    boolean existsByUserName(String userName);

    /**
     * Check if user exists by email
     * @param email Email
     * @return True if exists
     */
    boolean existsByEmail(String email);
}
