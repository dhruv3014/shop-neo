package com.shopneo.user.service;

import com.shopneo.user.dto.request.LoginUserRequest;
import com.shopneo.user.dto.request.RegisterUserRequest;
import com.shopneo.user.dto.response.AuthResponse;
import com.shopneo.user.entity.Role;
import com.shopneo.user.entity.User;
import com.shopneo.user.entity.UserCredentials;
import com.shopneo.user.exceptions.UnauthorizedException;
import com.shopneo.user.repository.RoleRepository;
import com.shopneo.user.repository.UserCredentialsRepository;
import com.shopneo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class LocalAuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserCredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    private static final String DEFAULT_ROLE = "USER";

    public AuthResponse login(LoginUserRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(UnauthorizedException::new);

        UserCredentials creds = credentialsRepository.findByUser(user)
                .orElseThrow(UnauthorizedException::new);

        if (!passwordEncoder.matches(request.getPassword(), creds.getPasswordHash())) {
            throw new UnauthorizedException();
        }

        user.setLastLoginAt(Instant.now());
        userRepository.save(user);

        return tokenService.issueTokens(user, request.getDeviceId());
    }

    @Transactional
    public void register(RegisterUserRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UnauthorizedException("User already exists");
        }

        Role role = roleRepository.findByName(DEFAULT_ROLE)
                .orElseThrow();

        User user = new User();
        user.setEmail(request.getEmail());
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setStatus(User.UserStatus.ACTIVE);
        user.setLastLoginAt(Instant.now());
        user.getRoles().add(role);

        userRepository.save(user);

        UserCredentials creds = new UserCredentials();
        creds.setUser(user);
        creds.setPasswordHash(passwordEncoder.encode(request.getPassword()));

        credentialsRepository.save(creds);
    }


}

