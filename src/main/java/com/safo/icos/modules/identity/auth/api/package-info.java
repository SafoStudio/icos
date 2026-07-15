/**
 * Public API contracts for the authentication module.
 * Contains interfaces and DTOs that other modules depend on
 * to interact with authentication functionality:
 * <ul>
 *   <li>{@link com.safo.icos.modules.identity.auth.api.AuthTokenProvider} —
 *       contract for access token generation and validation.</li>
 *   <li>{@link com.safo.icos.modules.identity.auth.api.dto.AuthTokenDto} —
 *       DTO returned by the token provider.</li>
 *   <li>{@link com.safo.icos.modules.identity.auth.api.constant.AuthErrorCode} —
 *       standardised error codes for authentication failures.</li>
 * </ul>
 * Implementations of these interfaces live in the
 * {@code infrastructure} layer (e.g., {@code JwtService} in the security module).
 */
package com.safo.icos.modules.identity.auth.api;
