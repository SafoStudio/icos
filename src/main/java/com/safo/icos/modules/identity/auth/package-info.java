/**
 * Authentication module.
 * <p>
 * Provides user registration, login, token refresh, and logout
 * via GraphQL endpoints. Implements a two-token authentication scheme:
 * <ul>
 *   <li><strong>Access token</strong> — short-lived JWT for API authentication.</li>
 *   <li><strong>Refresh token</strong> — long-lived opaque token stored as SHA-256 hash,
 *       delivered via HTTP-only cookie, rotated on each use.</li>
 * </ul>
 * <p>
 * <h3>Module layers</h3>
 * <ul>
 *   <li>{@code api} — contracts for other modules ({@code AuthTokenProvider}).</li>
 *   <li>{@code domain} — {@code RefreshToken} entity, {@code AuthDomainService}.</li>
 *   <li>{@code application} — {@code AuthService} orchestrating authentication flows.</li>
 *   <li>{@code infrastructure} — JPA repositories, password encoder.</li>
 *   <li>{@code presentation} — GraphQL resolver.</li>
 * </ul>
 *
 * @see com.safo.icos.modules.identity.auth.api.AuthTokenProvider
 * @see com.safo.icos.modules.identity.auth.application.AuthService
 */
package com.safo.icos.modules.identity.auth;