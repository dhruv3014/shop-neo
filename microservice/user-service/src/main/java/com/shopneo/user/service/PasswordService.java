package com.shopneo.user.service;

import com.shopneo.user.dto.request.ChangeUserPasswordRequest;
import com.shopneo.user.dto.request.ResetPasswordRequest;
import com.shopneo.user.entity.PasswordResetToken;
import com.shopneo.user.entity.User;
import com.shopneo.user.entity.UserCredentials;
import com.shopneo.user.exceptions.UnauthorizedException;
import com.shopneo.user.repository.PasswordResetTokenRepository;
import com.shopneo.user.repository.UserCredentialsRepository;
import com.shopneo.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private static final int TOKEN_EXPIRY_MINUTES = 30;

    private final UserRepository userRepository;
    private final UserCredentialsRepository credentialsRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Transactional
    public void forgotPassword(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {

            String tokenId = UUID.randomUUID().toString();
            String rawToken = UUID.randomUUID() + UUID.randomUUID().toString();
            PasswordResetToken resetToken = PasswordResetToken.builder()
                    .tokenId(tokenId)
                    .userId(user.getId())
                    .tokenHash(passwordEncoder.encode(rawToken))
                    .expiresAt(Instant.now().plus(TOKEN_EXPIRY_MINUTES, ChronoUnit.MINUTES))
                    .createdAt(Instant.now())
                    .build();


            tokenRepository.save(resetToken);

            mailService.sendPasswordResetMailHtml(
                    user.getEmail(),
                    tokenId + "." + rawToken
            );
        });
    }

    @Transactional
    public void resetPassword(String token, ResetPasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        String[] parts = token.split("\\.");
        if (parts.length != 2) throw new UnauthorizedException();

        String tokenId = parts[0];
        String rawToken = parts[1];

        PasswordResetToken resetToken = tokenRepository.findByTokenIdAndUsedAtIsNullAndExpiresAtAfter(tokenId, Instant.now())
                .filter(t -> passwordEncoder.matches(rawToken, t.getTokenHash()))
                .orElseThrow(() -> new UnauthorizedException("Invalid or expired token"));

        UserCredentials userCredentials = credentialsRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found"));

        userCredentials.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        credentialsRepository.save(userCredentials);

        resetToken.setUsedAt(Instant.now());
        tokenRepository.save(resetToken);
    }

    @Transactional
    public void changeUserPassword(String userId, ChangeUserPasswordRequest request) {

        var user = userRepository.findById(userId)
                .orElseThrow(UnauthorizedException::new);

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        UserCredentials userCredentials = credentialsRepository.findById(userId)
                .orElse(createCredentials(user));

        if(!passwordEncoder.matches(request.getCurrentPassword(), userCredentials.getPasswordHash())) {
            throw new IllegalArgumentException("Incorrect Password");
        }
        userCredentials.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        credentialsRepository.save(userCredentials);
    }

    private UserCredentials createCredentials(User user) {
        var credentials = new UserCredentials();
        credentials.setUser(user);
        return credentials;
    }
}

