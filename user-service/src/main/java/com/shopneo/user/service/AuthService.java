package com.shopneo.user.service;

import com.shopneo.user.config.JwtConfig;
import com.shopneo.user.dto.request.LoginUserRequest;
import com.shopneo.user.dto.request.RefreshTokenRequest;
import com.shopneo.user.dto.request.RegisterUserRequest;
import com.shopneo.user.dto.response.AuthResponse;
import com.shopneo.user.entities.RefreshToken;
import com.shopneo.user.entities.Role;
import com.shopneo.user.entities.User;
import com.shopneo.user.exceptions.UnauthorizedException;
import com.shopneo.user.repository.RefreshTokenRepository;
import com.shopneo.user.repository.RoleRepository;
import com.shopneo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository tokenRepository;
  private final RoleRepository roleRepository;
  private final JwtService jwtService;
  private final JwtConfig jwtConfig;
  private final PasswordEncoder passwordEncoder;

  private static final String DEFAULT_ROLE = "USER";

  // ---------- REGISTER ----------
  public void register(RegisterUserRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new IllegalArgumentException("User already exists");
    }

    Role role = roleRepository.findByName(DEFAULT_ROLE)
        .orElseThrow(() -> new IllegalStateException("Role not found"));

    User user = new User();
    user.setEmail(request.getEmail());
    user.setPassword(passwordEncoder.encode(request.getPassword()));
    user.setEnabled(true);
    user.getRoles().add(role.getName());

    userRepository.save(user);
  }

  // ---------- LOGIN ----------
  @Transactional
  public AuthResponse login(LoginUserRequest request) {
    User user = userRepository.findByEmail(request.getEmail())
        .orElseThrow(UnauthorizedException::new);

    if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
      throw new UnauthorizedException();
    }

    return generateTokens(user);
  }

  // ---------- REFRESH TOKEN ----------
  @Transactional
  public AuthResponse refreshToken(RefreshTokenRequest request) {

    RefreshToken storedToken = tokenRepository.findAll()
        .stream()
        .filter(token ->
            passwordEncoder.matches(
                request.getRefreshToken(),
                token.getTokenHash()))
        .findFirst()
        .orElseThrow(UnauthorizedException::new);

    if (storedToken.getExpiresAt().isBefore(Instant.now())) {
      tokenRepository.delete(storedToken);
      throw new UnauthorizedException();
    }

    User user = storedToken.getUser();

    // Rotate token
    tokenRepository.delete(storedToken);

    return generateTokens(user);
  }

  // ---------- TOKEN GENERATION ----------
  @Transactional
  public AuthResponse generateTokens(User user) {

    tokenRepository.deleteByUser(user);

    String accessToken = jwtService.generateAccessToken(user);
    String refreshTokenValue = UUID.randomUUID().toString();

    RefreshToken refreshToken = new RefreshToken();
    refreshToken.setUser(user);
    refreshToken.setTokenHash(passwordEncoder.encode(refreshTokenValue));
    refreshToken.setExpiresAt(
        Instant.now().plusSeconds(jwtConfig.getRefreshTokenTtl())
    );

    tokenRepository.save(refreshToken);

    return AuthResponse.builder()
        .accessToken(accessToken)
        .refreshToken(refreshTokenValue)
        .tokenType("Bearer")
        .expiresIn(jwtConfig.getAccessTokenTtl())
        .build();
  }
}
