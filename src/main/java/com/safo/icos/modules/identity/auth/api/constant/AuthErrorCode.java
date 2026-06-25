package com.safo.icos.modules.identity.auth.api.constant;

public final class AuthErrorCode {
  public static final String INVALID_CREDENTIALS = "AUTH_001";
  public static final String TOKEN_EXPIRED = "AUTH_002";
  public static final String TOKEN_REVOKED = "AUTH_003";
  public static final String TOKEN_NOT_FOUND = "AUTH_004";

  private AuthErrorCode() {
  }
}
