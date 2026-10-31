package com.safo.icos.security.jwt;

import com.safo.icos.modules.identity.auth.api.AuthTokenProvider;
import com.safo.icos.modules.identity.auth.api.dto.AuthTokenDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * JWT implementation of {@link AuthTokenProvider}.
 * Generates and validates HMAC-SHA signed access tokens
 * containing {@code sub} (userId) and {@code tenantId} claims.
 */
@Slf4j
@Service
public class JwtService implements AuthTokenProvider {

  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.access-token-ttl:900}")
  private int accessTokenTtlSeconds;

  private SecretKey signingKey;

  /** Initialises the HMAC signing key from the configured secret. */
  @PostConstruct
  public void init() {
    signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Generates a short-lived JWT access token for the given user and tenant.
   *
   * @param userId   the user's UUID
   * @param tenantId the tenant (company) UUID
   * @return DTO with the signed JWT, type, and TTL
   */
  @Override
  public AuthTokenDto generateAccessToken(UUID userId, UUID tenantId) {
    String accessToken = generateAccessTokenValue(userId, tenantId);
    return AuthTokenDto.builder()
            .accessToken(accessToken)
            .tokenType("Bearer")
            .expiresIn(accessTokenTtlSeconds)
            .build();
  }

  /**
   * Validates a JWT token's signature and expiration.
   *
   * @param token the JWT string
   * @return {@code true} if the token is valid and not expired
   */
  @Override
  public boolean validateToken(String token) {
    try {
      var claims = Jwts.parser()
              .verifyWith(signingKey)
              .build()
              .parseSignedClaims(token)
              .getPayload();
      return !claims.getExpiration().before(new Date());
    } catch (Exception e) {
      return false;
    }
  }

  /**
   * Extracts the user ID (subject) from a JWT.
   *
   * @param token a valid JWT
   * @return the user UUID as a string
   */
  @Override
  public String extractUserId(String token) {
    return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
  }

  /**
   * Extracts the tenant ID claim from a JWT.
   *
   * @param token a valid JWT
   * @return the tenant UUID as a string
   */
  @Override
  public String extractTenantId(String token) {
    return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("tenantId", String.class);
  }

  private String generateAccessTokenValue(UUID userId, UUID tenantId) {
    Instant now = Instant.now();
    Instant expiry = now.plusSeconds(accessTokenTtlSeconds);

    return Jwts.builder()
            .subject(userId.toString())
            .claim("tenantId", tenantId.toString())
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .signWith(signingKey)
            .compact();
  }
}
