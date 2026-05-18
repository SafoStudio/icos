package com.safo.icos.modules.user.domain.model;

import com.safo.icos.modules.user.domain.exception.InvalidNameException;
import lombok.Getter;
import java.util.Objects;

@Getter
public class UserName {
    private final String firstName;
    private final String lastName;

    private UserName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static UserName of(String firstName, String lastName) {
        if (firstName == null || firstName.isBlank()) {
            throw new InvalidNameException(firstName, lastName, "First name cannot be empty");
        }
        if (firstName.length() > 100) {
            throw new InvalidNameException(firstName, lastName, "First name cannot exceed 100 characters");
        }
        if (lastName == null || lastName.isBlank()) {
            throw new InvalidNameException(firstName, lastName, "Last name cannot be empty");
        }
        if (lastName.length() > 100) {
            throw new InvalidNameException(firstName, lastName, "Last name cannot exceed 100 characters");
        }
        return new UserName(firstName.trim(), lastName.trim());
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        UserName userName = (UserName) object;
        return Objects.equals(firstName, userName.firstName) &&
                Objects.equals(lastName, userName.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }
}