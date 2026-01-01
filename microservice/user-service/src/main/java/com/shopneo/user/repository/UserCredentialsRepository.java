package com.shopneo.user.repository;

import com.shopneo.user.entity.User;
import com.shopneo.user.entity.UserCredentials;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserCredentialsRepository extends JpaRepository<UserCredentials, String> {

    Optional<UserCredentials> findByUser(User user);
}
