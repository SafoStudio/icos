package com.safo.icos.modules.user.domain.exception;

import com.safo.icos.modules.user.domain.model.UserId;

public class UserNotFoundException extends DomainException {
    private static final String ERROR_CODE = "USER_NOT_FOUND";

    public UserNotFoundException(UserId userId) {
        super("User not found with id: " + userId.getValue(), ERROR_CODE);
    }

    public UserNotFoundException(String email) {
        super("User not found with email: " + email, ERROR_CODE);
    }
}