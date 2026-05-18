package com.safo.icos.modules.user.domain.exception;

import com.safo.icos.modules.user.domain.model.UserStatus;

public class UserStatusTransitionException extends DomainException {
    private static final String ERROR_CODE = "USER_STATUS_TRANSITION_INVALID";

    public UserStatusTransitionException(UserStatus from, UserStatus to) {
        super("Cannot transition user from " + from + " to " + to, ERROR_CODE);
    }
}