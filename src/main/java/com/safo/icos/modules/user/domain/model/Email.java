package com.safo.icos.modules.user.domain.model;

import com.safo.icos.modules.user.domain.exception.InvalidEmailException;
import lombok.Getter;
import java.util.Objects;

@Getter
public class Email {
    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidEmailException(value, "Email cannot be empty");
        }
        if (!value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new InvalidEmailException(value, "Invalid email format");
        }
        return new Email(value.toLowerCase().trim());
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Email email = (Email) object;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}