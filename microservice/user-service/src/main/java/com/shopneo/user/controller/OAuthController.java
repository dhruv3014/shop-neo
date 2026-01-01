package com.shopneo.user.controller;

import com.shopneo.user.dto.request.LoginUserRequest;
import com.shopneo.user.dto.response.AuthResponse;
import com.shopneo.user.model.AuthProvider;
import com.shopneo.user.service.OAuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/oauth")
@RequiredArgsConstructor
public class OAuthController {

  private final OAuthService oAuthService;

  // ---------------- LOGIN ----------------
  @PostMapping("/google")
  public ResponseEntity<AuthResponse> login(
      @Valid @RequestBody LoginUserRequest request) {

    AuthResponse response = oAuthService.oauthLogin(
            AuthProvider.GOOGLE,
            "dummy_id",
            "dummy@xyz.com",
            "Dummy",
            "User");
    return ResponseEntity.ok(response);
  }
}
