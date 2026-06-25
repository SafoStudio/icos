package com.safo.icos.modules.identity.auth.domain.exception;

import com.safo.icos.modules.identity.auth.api.constant.AuthErrorCode;

@SuppressWarnings("squid:S110")
public class InvalidCredentialsException extends AuthDomainException {
  public InvalidCredentialsException(String message) {
    super(message, AuthErrorCode.INVALID_CREDENTIALS);
  }
}
