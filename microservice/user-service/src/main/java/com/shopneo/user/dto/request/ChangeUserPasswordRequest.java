package com.shopneo.user.dto.request;

import lombok.Data;

@Data
public class ChangeUserPasswordRequest {
    private String currentPassword;
    private String newPassword;
    private String confirmPassword;
}

