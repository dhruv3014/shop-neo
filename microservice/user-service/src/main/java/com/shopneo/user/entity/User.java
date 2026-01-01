package com.shopneo.user.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(unique = true)
  private String username;

  @Column(unique = true)
  private String email;

  private String firstName;
  private String lastName;

  private String phone;

  private String avatarUrl;
  private String profilePhotoUrl;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private UserStatus status = UserStatus.ACTIVE;

  private boolean emailVerified;

  private Instant lastLoginAt;
  @UpdateTimestamp
  private Instant createdAt;
  @CreationTimestamp
  private Instant updatedAt;

  @ManyToMany(fetch = FetchType.LAZY)
  @JoinTable(
          name = "user_roles",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "role_id")
  )
  private Set<Role> roles = new HashSet<>();

  public static enum UserStatus{
    ACTIVE,
    INACTIVE,
    BLOCKED
  }
}
