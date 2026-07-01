package com.safo.icos.modules.identity.auth.api;

import com.safo.icos.modules.identity.auth.api.dto.AuthTokenDto;

import java.util.UUID;

/**
 * Contract for access token generation and validation.
 * Responsible for creating and verifying short-lived JWT access tokens
 * used to authenticate GraphQL API requests.
 * Refresh tokens are managed separately by {@code AuthDomainService}
 * and are <strong>not</strong> the responsibility of this interface.
 *
 * @see com.safo.icos.modules.identity.auth.domain.service.AuthDomainService
 */
public interface AuthTokenProvider {

  /**
   * Generates a JWT access token for the given user and tenant.
   *
   * @param userId   the authenticated user's unique identifier
   * @param tenantId the tenant (company) identifier to embed in the token
   * @return a DTO containing the access token, its type, and its TTL in seconds
   */
  AuthTokenDto generateAccessToken(UUID userId, UUID tenantId);

  /**
   * Validates the integrity and expiration of a JWT token.
   * Checks the cryptographic signature and ensures the token has not expired.
   *
   * @param token the JWT string to validate
   * @return {@code true} if the token is valid and not expired
   */
  boolean validateToken(String token);

  /**
   * Extracts the user ID (subject claim) from a valid JWT.
   *
   * @param token a valid JWT string
   * @return the user UUID as a string
   */
  String extractUserId(String token);

  /**
   * Extracts the tenant ID claim from a valid JWT.
   *
   * @param token a valid JWT string
   * @return the tenant UUID as a string
   */
  String extractTenantId(String token);
}