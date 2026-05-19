package com.safo.icos.modules.auth.domain.exception;

import com.safo.icos.common.constant.ErrorCode;

public class TokenExpiredException extends AuthDomainException {
    public TokenExpiredException(String message) {
        super(message, ErrorCode.TOKEN_EXPIRED);
    }
}
