package com.shopneo.user.repository;

import com.shopneo.user.entities.RefreshToken;
import com.shopneo.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository
    extends JpaRepository<RefreshToken, String> {

  Optional<RefreshToken> findByTokenHash(String tokenHash);

  void deleteByUser(User user);
}
