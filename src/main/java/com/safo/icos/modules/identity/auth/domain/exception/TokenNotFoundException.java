package com.safo.icos.modules.identity.auth.domain.exception;

import com.safo.icos.common.exception.NotFoundException;

public class TokenNotFoundException extends NotFoundException {
  public TokenNotFoundException(String message) {
    super(message);
  }
}
