package com.shopneo.user.controller;

import com.shopneo.user.dto.request.*;
import com.shopneo.user.dto.response.AuthResponse;
import com.shopneo.user.service.LocalAuthService;
import com.shopneo.user.service.PasswordService;
import com.shopneo.user.service.TokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class LocalAuthController {

  private final TokenService tokenService;
  private final LocalAuthService authService;
  private final PasswordService passwordService;

  @PostMapping("/register")
  public ResponseEntity<Void> register(
      @Valid @RequestBody RegisterUserRequest request) {

    authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
      @Valid @RequestBody LoginUserRequest request) {

    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<AuthResponse> refreshToken(
      @Valid @RequestBody RefreshTokenRequest request) {

    AuthResponse response = tokenService.refresh(request);
    return ResponseEntity.ok(response);
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Void> forgotPassword(
          @RequestBody ForgotPasswordRequest request) {
    passwordService.forgotPassword(request.getEmail());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(
          @RequestParam String token,
          @RequestBody ResetPasswordRequest request) {
    passwordService.resetPassword(token, request);
    return ResponseEntity.ok().build();
  }
}
