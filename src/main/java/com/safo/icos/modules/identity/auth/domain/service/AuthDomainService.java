package com.safo.icos.modules.identity.auth.domain.service;

import com.safo.icos.modules.identity.auth.domain.exception.TokenExpiredException;
import com.safo.icos.modules.identity.auth.domain.exception.TokenNotFoundException;
import com.safo.icos.modules.identity.auth.domain.exception.TokenRevokedException;
import com.safo.icos.modules.identity.auth.domain.model.RefreshToken;
import com.safo.icos.modules.identity.auth.domain.model.TokenValue;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

/**
 * Domain service for refresh token lifecycle management.
 * Handles creation and validation of refresh tokens.
 * Tokens are stored as SHA-256 hashes — raw values are never persisted.
 */
@Service
@RequiredArgsConstructor
public class AuthDomainService {

  /**
   * Creates a new refresh token for the specified membership.
   *
   * @param membershipId : User membership ID
   * @param deviceInfo : Device information
   * @param ttl : Token lifetime
   * @return a result containing the token entity and the raw value to send to the client
   */
  public RefreshTokenCreationResult createRefreshToken(UUID membershipId, String deviceInfo, Duration ttl) {
    String rawToken = TokenValue.generateRaw();
    TokenValue tokenHash = TokenValue.fromRaw(rawToken);
    Instant expiresAt = Instant.now().plus(ttl);

    RefreshToken entity = RefreshToken.create(membershipId, tokenHash, deviceInfo, expiresAt);
    return new RefreshTokenCreationResult(entity, rawToken);
  }

  /**
   * Checks if the refresh token is valid.
   *
   * @param token : token domain model
   * @throws TokenNotFoundException if the token is not found
   * @throws TokenRevokedException if the token is revoked
   * @throws TokenExpiredException if the token has expired
   */
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