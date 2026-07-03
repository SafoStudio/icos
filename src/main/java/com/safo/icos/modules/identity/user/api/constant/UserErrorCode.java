package com.safo.icos.modules.identity.user.api.constant;

/**
 * User and membership error codes.
 * Each constant represents a specific error that can be returned
 * to the client in a {@link com.safo.icos.common.exception.BusinessException}.
 */
public final class UserErrorCode {
  /** User not found by ID or email. */
  public static final String USER_NOT_FOUND = "USER_001";
  /** Email address is already in use. */
  public static final String EMAIL_ALREADY_EXISTS = "USER_002";
  /** User does not have an active membership. */
  public static final String USER_NOT_ACTIVE = "MEMBERSHIP_002";

  private UserErrorCode() {
  }
}