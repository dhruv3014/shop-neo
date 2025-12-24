package com.shopneo.common.authorization.model;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

/**
 * Represents the authenticated user extracted from JWT claims.
 * This object is available via the {@code @UserPrincipal} annotation in controllers.
 */
@Data
@Builder
public class AuthenticatedUser {

  /**
   * User ID (from JWT 'sub' claim).
   */
  private String id;

  /**
   * User email (from JWT 'email' claim).
   */
  private String email;

  private String name;

  /**
   * User roles (from JWT 'roles' claim).
   */
  private Set<String> roles;

  /**
   * The raw JWT token (without 'Bearer ' prefix).
   */
  private String token;

  /**
   * Checks if the user has a specific role.
   *
   * @param role the role to check
   * @return true if the user has the role
   */
  public boolean hasRole(String role) {
    return roles != null && roles.contains(role);
  }

  /**
   * Checks if the user has any of the specified roles.
   *
   * @param rolesToCheck the roles to check
   * @return true if the user has at least one of the roles
   */
  public boolean hasAnyRole(String... rolesToCheck) {
    if (roles == null) {
      return false;
    }
    for (String role : rolesToCheck) {
      if (roles.contains(role)) {
        return true;
      }
    }
    return false;
  }
}