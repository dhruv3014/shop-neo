package com.shopneo.user.entity;


import com.shopneo.user.model.AuthProvider;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(
        name = "user_auth_providers",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"provider", "providerUserId"}
        )
)
@Data
public class UserAuthProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    private AuthProvider provider;

    @Column(nullable = false)
    private String providerUserId;

//    private String providerEmail;
    @CreationTimestamp
    private Instant linkedAt;
}

