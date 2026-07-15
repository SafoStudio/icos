package com.safo.icos.modules.identity.auth.domain.service;

import com.safo.icos.modules.identity.auth.domain.model.RefreshToken;

/**
 * Result of creating a new refresh token.
 * Contains the domain entity (for storing in the database) and the raw token (for sending to the client).
 */
public record RefreshTokenCreationResult(RefreshToken entity, String rawToken) {
}
