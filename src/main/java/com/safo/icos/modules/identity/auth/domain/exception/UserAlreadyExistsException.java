package com.safo.icos.modules.identity.auth.domain.exception;

import com.safo.icos.modules.identity.user.api.constant.UserErrorCode;

@SuppressWarnings("squid:S110")
public class UserAlreadyExistsException extends AuthDomainException {
  public UserAlreadyExistsException(String message) {
    super(message, UserErrorCode.EMAIL_ALREADY_EXISTS);
  }
}
