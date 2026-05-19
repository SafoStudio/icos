package com.safo.icos.modules.auth.domain.exception;

import com.safo.icos.common.exception.BusinessException;

public class AuthDomainException extends BusinessException {
    public AuthDomainException(String message, String errorCode) {
        super(message, errorCode);
    }
}