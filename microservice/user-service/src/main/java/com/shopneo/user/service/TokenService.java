package com.shopneo.user.service;

import com.shopneo.user.config.JwtConfig;
import com.shopneo.user.dto.request.RefreshTokenRequest;
import com.shopneo.user.dto.response.AuthResponse;
import com.shopneo.user.entity.RefreshToken;
import com.shopneo.user.entity.User;
import com.shopneo.user.exceptions.UnauthorizedException;
import com.shopneo.user.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final RefreshTokenRepository tokenRepository;
    private final JwtService jwtService;
    private final JwtConfig jwtConfig;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public AuthResponse issueTokens(User user, String deviceId) {

        String accessToken = jwtService.generateAccessToken(user);

        String refreshTokenValue = UUID.randomUUID().toString();
        String tokenId = UUID.randomUUID().toString();

        RefreshToken token = new RefreshToken();
        token.setUser(user);
        token.setTokenId(tokenId);
        token.setTokenHash(passwordEncoder.encode(refreshTokenValue));
        token.setDeviceId(deviceId);
        token.setCreatedAt(Instant.now());
        token.setExpiresAt(
                Instant.now().plusSeconds(jwtConfig.getRefreshTokenTtl())
        );

        tokenRepository.save(token);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(tokenId + "." + refreshTokenValue)
                .tokenType("Bearer")
                .expiresIn(jwtConfig.getAccessTokenTtl())
                .build();
    }


    @Transactional
    public AuthResponse refresh(RefreshTokenRequest request) {

        String[] parts = request.getRefreshToken().split("\\.");
        if (parts.length != 2) throw new UnauthorizedException();

        String tokenId = parts[0];
        String tokenValue = parts[1];

        RefreshToken token = tokenRepository.findByTokenId(tokenId)
                .orElseThrow(UnauthorizedException::new);

        if (token.getExpiresAt().isBefore(Instant.now())) {
            tokenRepository.delete(token);
            throw new UnauthorizedException();
        }

        if (!passwordEncoder.matches(tokenValue, token.getTokenHash())) {
            tokenRepository.delete(token);
            throw new UnauthorizedException();
        }

        User user = token.getUser();

        // rotate
        tokenRepository.delete(token);

        return issueTokens(user, token.getDeviceId());
    }

}
