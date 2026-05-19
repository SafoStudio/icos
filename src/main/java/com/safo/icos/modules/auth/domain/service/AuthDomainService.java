package com.safo.icos.modules.auth.domain.service;

import com.safo.icos.modules.auth.domain.exception.TokenExpiredException;
import com.safo.icos.modules.auth.domain.exception.TokenNotFoundException;
import com.safo.icos.modules.auth.domain.exception.TokenRevokedException;
import com.safo.icos.modules.auth.domain.model.RefreshToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.time.Instant;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthDomainService {

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    public String generateTokenHash() {
        byte[] bytes = new byte[32];
        SECURE_RANDOM.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public RefreshToken createRefreshToken(UUID membershipId, String deviceInfo, Duration ttl) {
        String tokenHash = generateTokenHash();
        Instant expiresAt = Instant.now().plus(ttl);

        return RefreshToken.create(membershipId, tokenHash, deviceInfo, expiresAt);
    }

    public void validateRefreshToken(RefreshToken token) {
        if (token == null) {
            throw new TokenNotFoundException("Refresh token not found");
        }
        if (token.isRevoked()) {
            throw new TokenRevokedException("Refresh token has been revoked");
        }
        if (!token.isValid()) {
            throw new TokenExpiredException("Refresh token has expired");
        }
    }
}