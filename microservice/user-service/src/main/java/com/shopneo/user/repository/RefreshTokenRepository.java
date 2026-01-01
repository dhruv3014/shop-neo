package com.shopneo.user.repository;

import com.shopneo.user.entity.RefreshToken;
import com.shopneo.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository
    extends JpaRepository<RefreshToken, String> {

  Optional<RefreshToken> findByTokenId(String tokenId);

  void deleteByUser(User user);
}
