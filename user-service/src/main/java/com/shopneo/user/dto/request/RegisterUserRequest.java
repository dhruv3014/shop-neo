package com.shopneo.user.dto.request;

import lombok.Data;

@Data
public class RegisterUserRequest {

  private String email;
  private String password;

}
