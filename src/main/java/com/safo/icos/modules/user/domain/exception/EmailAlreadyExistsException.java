package com.safo.icos.modules.user.domain.exception;

import com.safo.icos.modules.user.domain.model.Email;
import java.util.UUID;

public class EmailAlreadyExistsException extends DomainException {
    private static final String ERROR_CODE = "EMAIL_ALREADY_EXISTS";

    public EmailAlreadyExistsException(Email email, UUID companyId) {
        super("Email " + email.getValue() + " already exists in company: " + companyId, ERROR_CODE);
    }

    public EmailAlreadyExistsException(Email email) {
        super("Email " + email.getValue() + " already exists in system", ERROR_CODE);
    }
}