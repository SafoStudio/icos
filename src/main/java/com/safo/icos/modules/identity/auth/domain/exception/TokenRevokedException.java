package com.safo.icos.modules.identity.auth.domain.exception;

import com.safo.icos.modules.identity.auth.api.constant.AuthErrorCode;

@SuppressWarnings("squid:S110")
public class TokenRevokedException extends AuthDomainException {
  public TokenRevokedException(String message) {
    super(message, AuthErrorCode.TOKEN_REVOKED);
  }
}
