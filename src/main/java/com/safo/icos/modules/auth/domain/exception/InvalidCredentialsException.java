package com.safo.icos.modules.auth.domain.exception;

import com.safo.icos.common.constant.ErrorCode;

@SuppressWarnings("squid:S110")
public class InvalidCredentialsException extends AuthDomainException {
  public InvalidCredentialsException(String message) {
    super(message, ErrorCode.INVALID_CREDENTIALS);
  }
}