package com.shopneo.user.exceptions;

public class UnauthorizedException extends RuntimeException{

  // extra property if we want to manage
  public UnauthorizedException(){
    super("User is not authorized!!");
  }

  public UnauthorizedException(String message) {
    super(message);
  }
}
