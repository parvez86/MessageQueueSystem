package com.example.MessageQueueConsumer.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing a user in the system
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User extends Base {
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "uid", unique = true, nullable = false)
    private String uid;
    
    @Column(name = "user_name", unique = true, nullable = false)
    private String userName;
    
    @Column(name = "email", unique = true, nullable = false)
    private String email;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "phone_number")
    private String phoneNumber;
    
    @Enumerated(EnumType.ORDINAL)
    @Column(name = "status", nullable = false)
    private Status status;

    public void softDelete() {
        String truncatedName = this.userName;
        if(truncatedName.length()>245) {
            truncatedName = truncatedName.substring(0, 245);
        }

        truncatedName = "_deleted_"+truncatedName;
        setUserName(truncatedName);
        setStatus(Status.DELETED);
    }
}
