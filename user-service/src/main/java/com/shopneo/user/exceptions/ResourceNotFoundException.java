package com.shopneo.user.exceptions;

public class ResourceNotFoundException extends RuntimeException{

  // extra property if we want to manage
  public ResourceNotFoundException(){
    super("Resource not found on server !!");
  }

  public ResourceNotFoundException(String message) {
    super(message);
  }
}
