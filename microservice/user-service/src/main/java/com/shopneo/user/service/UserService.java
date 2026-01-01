package com.shopneo.user.service;

import com.shopneo.user.entity.User;
import com.shopneo.user.exceptions.ResourceNotFoundException;
import com.shopneo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

  private UserRepository userRepository;

  public User saveUser(User user) {
    return userRepository.save(user);
  }

  public List<User> getAllUser() {
    return userRepository.findAll();
  }

  public User getUser(String userId) {
    return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User with given id is not found on server !! : " + userId));
  }
}


