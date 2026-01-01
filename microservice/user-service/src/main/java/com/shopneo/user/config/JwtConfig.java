package com.shopneo.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@ConfigurationProperties(prefix = "jwt")
@Configuration
@Data
public class JwtConfig {

  private String secret;
  private long accessTokenTtl;   // seconds
  private long refreshTokenTtl;  // seconds
}
