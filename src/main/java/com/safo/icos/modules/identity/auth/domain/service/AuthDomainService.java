package com.safo.icos.modules.identity.auth.domain.service;

import com.safo.icos.modules.identity.auth.domain.exception.TokenExpiredException;
import com.safo.icos.modules.identity.auth.domain.exception.TokenNotFoundException;
import com.safo.icos.modules.identity.auth.domain.exception.TokenRevokedException;
import com.safo.icos.modules.identity.auth.domain.model.RefreshToken;
import com.safo.icos.modules.identity.auth.domain.model.TokenValue;
import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthDomainService {

  public RefreshToken createRefreshToken(UUID membershipId, String deviceInfo, Duration ttl) {
    TokenValue token = TokenValue.generate();
    Instant expiresAt = Instant.now().plus(ttl);

    return RefreshToken.create(membershipId, token, deviceInfo, expiresAt);
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
