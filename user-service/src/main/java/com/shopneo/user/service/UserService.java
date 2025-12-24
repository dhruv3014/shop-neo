package com.shopneo.user.service;

import com.shopneo.user.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface UserService {

  //get all user
  List<User> getAllUser();

  // get single user of given userId

  User getUser(String userId);

  // TODO: delete
  // TODO: update
}