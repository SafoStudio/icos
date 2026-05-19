package com.safo.icos.modules.auth.domain.model;

import lombok.Getter;
import java.util.Objects;
import java.util.UUID;

@Getter
public class RefreshTokenId {
    private final UUID value;

    private RefreshTokenId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("RefreshTokenId cannot be null");
        }
        this.value = value;
    }

    public static RefreshTokenId generate() {
        return new RefreshTokenId(UUID.randomUUID());
    }

    public static RefreshTokenId fromString(String value) {
        return new RefreshTokenId(UUID.fromString(value));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshTokenId that = (RefreshTokenId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}