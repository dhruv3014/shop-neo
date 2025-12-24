package com.shopneo.user.entities;

import jakarta.persistence.*;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@Table(name = "users")
public class User {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private String id;

  @Column(unique = true, nullable = false)
  private String email;

  @Column(nullable = false)
  private String password;

  private boolean enabled;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "user_roles")
  @Column(name = "role")
  private Set<String> roles = new HashSet<>();
}
