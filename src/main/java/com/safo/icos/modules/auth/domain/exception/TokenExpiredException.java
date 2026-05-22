package com.safo.icos.modules.auth.domain.exception;

import com.safo.icos.modules.auth.api.constant.AuthErrorCode;

@SuppressWarnings("squid:S110")
public class TokenExpiredException extends AuthDomainException {
  public TokenExpiredException(String message) {
    super(message, AuthErrorCode.TOKEN_EXPIRED);
  }
}
