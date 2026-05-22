package com.safo.icos.modules.auth.domain.exception;

import com.safo.icos.modules.auth.api.constant.AuthErrorCode;

@SuppressWarnings("squid:S110")
public class TokenRevokedException extends AuthDomainException {
  public TokenRevokedException(String message) {
    super(message, AuthErrorCode.TOKEN_REVOKED);
  }
}
