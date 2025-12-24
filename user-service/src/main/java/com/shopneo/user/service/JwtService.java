package com.shopneo.user.service;

import com.shopneo.user.config.JwtConfig;
import com.shopneo.user.entities.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtService {

  private final JwtConfig jwtConfig;

  public String generateAccessToken(User user) {
    return Jwts.builder()
        .setSubject(user.getId().toString())
        .claim("email", user.getEmail())
        .claim("roles", user.getRoles())
        .setIssuedAt(new Date())
        .setExpiration(Date.from(
            Instant.now().plusSeconds(jwtConfig.getAccessTokenTtl())))
        .signWith(Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes()))
        .compact();
  }

  public Claims validate(String token) {
    return Jwts.parserBuilder()
        .setSigningKey(jwtConfig.getSecret().getBytes())
        .build()
        .parseClaimsJws(token)
        .getBody();
  }
}
