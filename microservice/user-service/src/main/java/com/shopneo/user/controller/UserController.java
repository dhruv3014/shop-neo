package com.shopneo.user.controller;

import com.shopneo.common.authorization.model.AuthenticatedUser;
import com.shopneo.user.entity.User;
import com.shopneo.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    public UserService userService;

    @GetMapping
    public User editUserAddress(
            @AuthenticationPrincipal AuthenticatedUser user) {
        return userService.getUser(user.getId());
    }
}
