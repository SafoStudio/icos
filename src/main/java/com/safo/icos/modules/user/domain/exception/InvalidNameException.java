package com.safo.icos.modules.user.domain.exception;

public class InvalidNameException extends DomainException {
    private static final String ERROR_CODE = "INVALID_NAME";

    public InvalidNameException(String firstName, String lastName, String reason) {
        super("Invalid name. FirstName: " + firstName + ", LastName: " + lastName + ". Reason: " + reason, ERROR_CODE);
    }
}