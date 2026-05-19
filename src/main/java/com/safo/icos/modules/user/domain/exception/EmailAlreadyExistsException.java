package com.safo.icos.modules.user.domain.exception;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.common.constant.ErrorCode;
import com.safo.icos.modules.user.domain.model.Email;
import java.util.UUID;

public class EmailAlreadyExistsException extends BusinessException {
    public EmailAlreadyExistsException(Email email, UUID companyId) {
        super("Email already exists: " + email + " for company: " + companyId,
                ErrorCode.EMAIL_ALREADY_EXISTS);
    }
}