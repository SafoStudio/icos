package com.safo.icos.modules.auth.domain.exception;

import com.safo.icos.modules.auth.api.constant.AuthErrorCode;

@SuppressWarnings("squid:S110")
public class InvalidCredentialsException extends AuthDomainException {
  public InvalidCredentialsException(String message) {
    super(message, AuthErrorCode.INVALID_CREDENTIALS);
  }
}
