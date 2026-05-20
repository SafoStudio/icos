package com.safo.icos.modules.auth.domain.exception;

import com.safo.icos.common.constant.ErrorCode;

@SuppressWarnings("squid:S110")
public class TokenRevokedException extends AuthDomainException {
    public TokenRevokedException(String message) {
        super(message, ErrorCode.TOKEN_REVOKED);
    }
}