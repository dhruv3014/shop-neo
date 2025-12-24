package com.shopneo.user.controller;

import com.shopneo.user.dto.request.CreateRoleRequest;
import com.shopneo.user.entities.Role;
import com.shopneo.user.service.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth/internal")
@RequiredArgsConstructor
public class AuthInternalController {

  private final RoleService roleService;

  // ---------------- REGISTER ----------------
  @PostMapping("/roles")
  public ResponseEntity<Role> addRole(
      @Valid @RequestBody CreateRoleRequest request) {

    var newRole = roleService.createRole(request);
    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(newRole);
  }

}
