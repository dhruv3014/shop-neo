package com.shopneo.user.controller;

import com.shopneo.user.dto.request.LoginUserRequest;
import com.shopneo.user.dto.request.RefreshTokenRequest;
import com.shopneo.user.dto.request.RegisterUserRequest;
import com.shopneo.user.dto.response.AuthResponse;
import com.shopneo.user.service.LocalAuthService;
import com.shopneo.user.service.TokenService;
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
public class LocalAuthController {

  private final TokenService tokenService;
  private final LocalAuthService authService;


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
}
