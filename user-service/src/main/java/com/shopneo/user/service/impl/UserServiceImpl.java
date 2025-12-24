package com.shopneo.user.service.impl;

import com.shopneo.user.entities.User;
import com.shopneo.user.exceptions.ResourceNotFoundException;
import com.shopneo.user.repository.UserRepository;
import com.shopneo.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UserRepository userRepository;

  @Override
  public User saveUser(User user) {
    return userRepository.save(user);
  }

  @Override
  public List<User> getAllUser() {
    return userRepository.findAll();
  }

  @Override
  public User getUser(String userId) {
    return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given id is not found on server !! : " + userId));
  }
}


