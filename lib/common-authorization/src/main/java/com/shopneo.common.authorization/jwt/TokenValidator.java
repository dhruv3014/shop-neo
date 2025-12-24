package com.shopneo.common.authorization.jwt;

import com.shopneo.common.authorization.config.CommonAuthorizationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

/**
 * Validates JWT tokens using either HMAC shared secret or RSA public key.
 * This class is intended for token validation only (not issuance).
 */
@Slf4j
public class TokenValidator {

  private final JwtParser jwtParser;

  /**
   * Creates a TokenValidator from CommonAuthorizationProperties.
   * Supports both HMAC (jwtSecret) and RSA (jwtPublicKey) validation.
   *
   * @param properties the authorization properties containing JWT configuration
   * @throws IllegalStateException if neither secret nor public key is configured
   */
  public TokenValidator(CommonAuthorizationProperties properties) {
    if (properties.getJwtPublicKey() != null && !properties.getJwtPublicKey().isBlank()) {
      PublicKey publicKey = parsePublicKeyFromPem(properties.getJwtPublicKey());
      this.jwtParser = Jwts.parser()
          .verifyWith(publicKey)
          .build();
      log.info("TokenValidator initialized with RSA public key");
    } else if (properties.getJwtSecret() != null && !properties.getJwtSecret().isBlank()) {
      SecretKey secretKey = Keys.hmacShaKeyFor(properties.getJwtSecret().getBytes(StandardCharsets.UTF_8));
      this.jwtParser = Jwts.parser()
          .verifyWith(secretKey)
          .build();
      log.info("TokenValidator initialized with HMAC secret");
    } else {
      throw new IllegalStateException(
          "No JWT key configured. Set either 'common-authorization.jwt-secret' or 'common-authorization.jwt-public-key'");
    }
  }

  /**
   * Validates the given JWT token and returns its claims.
   *
   * @param token the JWT token to validate (without "Bearer " prefix)
   * @return the claims contained in the token
   * @throws io.jsonwebtoken.JwtException if the token is invalid, expired, or has an invalid signature
   */
  public Claims validate(String token) {
    return jwtParser.parseSignedClaims(token).getPayload();
  }

  /**
   * Parses a PEM-encoded public key string into a PublicKey object.
   *
   * @param pem the PEM-encoded public key
   * @return the parsed PublicKey
   * @throws IllegalArgumentException if the key cannot be parsed
   */
  private PublicKey parsePublicKeyFromPem(String pem) {
    try {
      String publicKeyPEM = pem
          .replace("-----BEGIN PUBLIC KEY-----", "")
          .replace("-----END PUBLIC KEY-----", "")
          .replaceAll("\\s", "");

      byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
      X509EncodedKeySpec keySpec = new X509EncodedKeySpec(decoded);

      KeyFactory keyFactory = KeyFactory.getInstance("RSA");
      return keyFactory.generatePublic(keySpec);
    } catch (Exception e) {
      throw new IllegalArgumentException("Failed to parse public key from PEM", e);
    }
  }
}