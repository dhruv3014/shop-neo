package com.shopneo.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Data
@Entity
@Table(name = "refresh_tokens", indexes = {
        @Index(columnList = "token_id"),
        @Index(columnList = "user_id")
})
public class RefreshToken {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(nullable = false, unique = true)
  private String tokenId; // public identifier

  @ManyToOne(fetch = FetchType.LAZY)
  private User user;

  private String tokenHash;

  private Instant expiresAt;

  @CreationTimestamp
  private Instant createdAt;

  private String deviceId;
}
