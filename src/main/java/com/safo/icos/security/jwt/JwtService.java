package com.safo.icos.security.jwt;

import com.safo.icos.modules.identity.auth.api.AuthTokenProvider;
import com.safo.icos.modules.identity.auth.api.dto.AuthTokenDto;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class JwtService implements AuthTokenProvider {

  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.access-token-ttl:900}")
  private int accessTokenTtlSeconds;

  private SecretKey signingKey;

  @PostConstruct
  public void init() {
    signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public AuthTokenDto generateAccessToken(UUID userId, UUID tenantId) {
    String accessToken = generateAccessTokenValue(userId, tenantId);
    return AuthTokenDto.builder()
            .accessToken(accessToken)
            .tokenType("Bearer")
            .expiresIn(accessTokenTtlSeconds)
            .build();
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

  @Override
  public String extractUserId(String token) {
    return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
  }

  @Override
  public String extractTenantId(String token) {
    return Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .get("tenantId", String.class);
  }
}