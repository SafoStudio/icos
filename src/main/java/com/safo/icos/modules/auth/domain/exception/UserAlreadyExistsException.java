package com.safo.icos.modules.auth.domain.exception;

import com.safo.icos.common.constant.ErrorCode;

public class UserAlreadyExistsException extends AuthDomainException {
    public UserAlreadyExistsException(String message) {
        super(message, ErrorCode.EMAIL_ALREADY_EXISTS);
    }
}