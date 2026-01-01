package com.shopneo.user.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "user_credentials")
@Data
public class UserCredentials {

    @Id
    private String userId;

    @OneToOne
    @MapsId
    private User user;

    private String passwordHash;

    @UpdateTimestamp
    private Instant updatedAt;
    @CreationTimestamp
    private Instant createdAt;
}

