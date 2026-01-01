package com.shopneo.user.service;

import com.shopneo.user.config.JwtConfig;
import com.shopneo.user.entity.Role;
import com.shopneo.user.entity.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtService {

  private final JwtConfig jwtConfig;

  public String generateAccessToken(User user) {
    return Jwts.builder()
        .setSubject(user.getId())
        .claim("email", user.getEmail())
        .claim("roles",
                user.getRoles().stream()
                        .map(Role::getName)
                        .toList())
        .setIssuedAt(new Date())
        .setExpiration(Date.from(
            Instant.now().plusSeconds(jwtConfig.getAccessTokenTtl())))
        .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
        .compact();
  }
}
