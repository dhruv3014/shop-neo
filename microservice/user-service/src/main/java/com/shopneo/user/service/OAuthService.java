package com.shopneo.user.service;

import com.shopneo.user.dto.response.AuthResponse;
import com.shopneo.user.entity.Role;
import com.shopneo.user.entity.User;
import com.shopneo.user.entity.UserAuthProvider;
import com.shopneo.user.model.AuthProvider;
import com.shopneo.user.repository.RoleRepository;
import com.shopneo.user.repository.UserAuthProviderRepository;
import com.shopneo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UserAuthProviderRepository userAuthProviderRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenService tokenService;

    @Transactional
    public AuthResponse oauthLogin(
            AuthProvider provider,
            String providerUserId,
            String email,
            String firstName,
            String lastName
    ) {
        User user = userAuthProviderRepository
                .findByProviderAndProviderUserId(provider, providerUserId)
                .map(UserAuthProvider::getUser)
                .orElseGet(() -> provisionUser(provider, providerUserId, email, firstName, lastName));

        return tokenService.issueTokens(user, "oauth");
    }

    @Transactional
    public User provisionUser(
            AuthProvider provider,
            String providerUserId,
            String email,
            String firstName,
            String lastName) {

        // Check if user already exists by email (account linking)
        User user = userRepository.findByEmail(email).orElse(null);

        if (user == null) {
            // Create new user
            user = new User();
            user.setEmail(email);
            user.setUsername(generateUsername(email));
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setStatus(User.UserStatus.ACTIVE);
            user.setEmailVerified(true);
            user.setCreatedAt(Instant.now());

            Role defaultRole = roleRepository.findByName("USER")
                    .orElseThrow();

            user.getRoles().add(defaultRole);

            userRepository.save(user);
        }

        // Link OAuth provider
        UserAuthProvider authProvider = new UserAuthProvider();
        authProvider.setUser(user);
        authProvider.setProvider(provider);
        authProvider.setProviderUserId(providerUserId);
//        authProvider.setProviderEmail(email);
        authProvider.setLinkedAt(Instant.now());

        userAuthProviderRepository.save(authProvider);

        return user;
    }

    public String generateUsername(String email) {

        String base = email
                .split("@")[0]
                .toLowerCase()
                .replaceAll("[^a-z0-9]", "");

        String suffix = UUID.randomUUID()
                .toString()
                .substring(0, 4);

        return base + "_" + suffix;
    }



}
