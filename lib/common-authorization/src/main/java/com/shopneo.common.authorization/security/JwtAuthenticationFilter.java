package com.shopneo.common.authorization.security;

import com.shopneo.common.authorization.config.CommonAuthorizationProperties;
import com.shopneo.common.authorization.jwt.TokenValidator;
import com.shopneo.common.authorization.model.AuthenticatedUser;
import com.shopneo.common.authorization.web.PathExclusionMatcher;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * JWT Authentication Filter that validates tokens and sets Spring Security context.
 * Supports configurable principal header, on-demand authorization, and path exclusions.
 */
@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private static final String BEARER_PREFIX = "Bearer ";

  private final TokenValidator tokenValidator;
  private final CommonAuthorizationProperties properties;
  private final PathExclusionMatcher pathExclusionMatcher;

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain) throws ServletException, IOException {

    // Skip excluded paths
    if (pathExclusionMatcher.isExcluded(request)) {
      log.debug("Path {} is excluded from authorization", request.getRequestURI());
      filterChain.doFilter(request, response);
      return;
    }

    String headerName = properties.getPrincipalHeader();
    String authHeader = request.getHeader(headerName);

    // Handle missing authorization header
    if (authHeader == null || authHeader.isBlank()) {
      if (properties.isOnDemandAuthorization()) {
        // On-demand: allow request without authentication
        log.debug("No {} header present, on-demand authorization allows pass-through", headerName);
        filterChain.doFilter(request, response);
        return;
      } else {
        // Mandatory: reject request
        log.warn("Missing {} header for mandatory authorization", headerName);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing authorization header");
        return;
      }
    }

    // Extract token (remove Bearer prefix if present)
    String token = authHeader.startsWith(BEARER_PREFIX)
        ? authHeader.substring(BEARER_PREFIX.length())
        : authHeader;

    try {
      Claims claims = tokenValidator.validate(token);

      // Extract roles and create authorities
      Set<GrantedAuthority> authorities = extractAuthorities(claims);

      // Extract roles without prefix for AuthenticatedUser
      Set<String> roles = extractRoles(claims);

      // Build AuthenticatedUser as principal for @AuthenticationPrincipal
      AuthenticatedUser authenticatedUser = AuthenticatedUser.builder()
          .id(claims.getSubject())
          .email(claims.get("email", String.class))
          .roles(roles)
          .token(token)
          .build();

      // Create authentication with AuthenticatedUser as principal
      UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
          authenticatedUser,
          null,
          authorities
      );

      SecurityContextHolder.getContext().setAuthentication(auth);
      log.debug("Successfully authenticated user: {}", claims.getSubject());

    } catch (JwtException e) {
      log.warn("JWT validation failed: {}", e.getMessage());
      SecurityContextHolder.clearContext();

      if (!properties.isOnDemandAuthorization()) {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
        return;
      }
      // On-demand: continue without authentication
    }

    filterChain.doFilter(request, response);
  }

  /**
   * Extracts authorities from JWT claims.
   * Looks for "roles" claim and prefixes each role with the configured rolePrefix.
   *
   * @param claims the JWT claims
   * @return set of granted authorities
   */
  @SuppressWarnings("unchecked")
  private Set<GrantedAuthority> extractAuthorities(Claims claims) {
    Object rolesObj = claims.get("roles");

    if (rolesObj == null) {
      return Collections.emptySet();
    }

    List<String> roles;
    if (rolesObj instanceof List) {
      roles = (List<String>) rolesObj;
    } else {
      return Collections.emptySet();
    }

    String rolePrefix = properties.getRolePrefix();

    return roles.stream()
        .map(role -> new SimpleGrantedAuthority(rolePrefix + role))
        .collect(Collectors.toSet());
  }

  /**
   * Extracts raw role names from JWT claims (without prefix).
   *
   * @param claims the JWT claims
   * @return set of role names
   */
  @SuppressWarnings("unchecked")
  private Set<String> extractRoles(Claims claims) {
    Object rolesObj = claims.get("roles");

    if (rolesObj == null) {
      return new HashSet<>();
    }

    if (rolesObj instanceof List) {
      return new HashSet<>((List<String>) rolesObj);
    }

    return new HashSet<>();
  }
}