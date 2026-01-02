package com.shopneo.user.controller;

import com.shopneo.common.authorization.model.AuthenticatedUser;
import com.shopneo.user.dto.request.ChangeUserPasswordRequest;
import com.shopneo.user.entity.User;
import com.shopneo.user.service.PasswordService;
import com.shopneo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordService passwordService;

    @GetMapping
    public User getUserProfile(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return userService.getUser(user.getId());
    }

    @PostMapping("/change-password")
    public ResponseEntity<Void> changePassword(
            @AuthenticationPrincipal AuthenticatedUser user,
            @RequestBody ChangeUserPasswordRequest request) {
        passwordService.changeUserPassword(user.getId(), request);
        return ResponseEntity.ok().build();
    }
}
