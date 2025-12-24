package com.shopneo.user.controller;

import com.shopneo.user.dto.request.LoginUserRequest;
import com.shopneo.user.dto.request.RefreshTokenRequest;
import com.shopneo.user.dto.request.RegisterUserRequest;
import com.shopneo.user.dto.response.AuthResponse;
import com.shopneo.user.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService authService;

  // ---------------- REGISTER ----------------
  @PostMapping("/register")
  public ResponseEntity<Void> register(
      @Valid @RequestBody RegisterUserRequest request) {

    authService.register(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  // ---------------- LOGIN ----------------
  @PostMapping("/login")
  public ResponseEntity<AuthResponse> login(
      @Valid @RequestBody LoginUserRequest request) {

    AuthResponse response = authService.login(request);
    return ResponseEntity.ok(response);
  }

  // ---------------- REFRESH TOKEN ----------------
  @PostMapping("/refresh-token")
  public ResponseEntity<AuthResponse> refreshToken(
      @Valid @RequestBody RefreshTokenRequest request) {

    AuthResponse response = authService.refreshToken(request);
    return ResponseEntity.ok(response);
  }
}
