package com.shopneo.user.dto.request;

import lombok.Data;

@Data
public class LoginUserRequest {

  private String email;
  private String password;
  private String deviceId;

}
