package com.safo.icos.modules.auth.domain.exception;

import com.safo.icos.modules.user.api.constant.UserErrorCode;

@SuppressWarnings("squid:S110")
public class UserAlreadyExistsException extends AuthDomainException {
  public UserAlreadyExistsException(String message) {
    super(message, UserErrorCode.EMAIL_ALREADY_EXISTS);
  }
}
