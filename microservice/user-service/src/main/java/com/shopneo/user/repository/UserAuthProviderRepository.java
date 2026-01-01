package com.shopneo.user.repository;

import com.shopneo.user.entity.UserAuthProvider;
import com.shopneo.user.model.AuthProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAuthProviderRepository extends JpaRepository<UserAuthProvider, String> {

    Optional<UserAuthProvider> findByProviderAndProviderUserId(AuthProvider authProvider, String providerUserId);
}
