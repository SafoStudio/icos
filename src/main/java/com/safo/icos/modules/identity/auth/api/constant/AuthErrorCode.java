package com.safo.icos.modules.identity.auth.api.constant;

/**
 * Authentication error codes.
 * Each constant represents a specific authentication-related error
 * that can be returned to the client in a {@link com.safo.icos.common.exception.BusinessException}.
 */
public final class AuthErrorCode {

  /** Invalid email or password provided during login. */
  public static final String INVALID_CREDENTIALS = "AUTH_001";

  /** The provided token has expired and is no longer valid. */
  public static final String TOKEN_EXPIRED = "AUTH_002";

  /** The token has been revoked (e.g., after logout or rotation). */
  public static final String TOKEN_REVOKED = "AUTH_003";

  /** The token was not found in the database (invalid or never existed). */
  public static final String TOKEN_NOT_FOUND = "AUTH_004";

  private AuthErrorCode() {
    // utility class — prevent instantiation
  }
}