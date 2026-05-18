package com.safo.icos.modules.user.domain.exception;

public class InvalidEmailException extends DomainException {
    private static final String ERROR_CODE = "INVALID_EMAIL";

    public InvalidEmailException(String invalidEmail, String reason) {
        super("Invalid email: " + invalidEmail + ". Reason: " + reason, ERROR_CODE);
    }
}