package com.safo.icos.security.permission;

import java.util.UUID;

/**
 * Holds both the old (database) state and the new (DTO) state
 * of a resource being modified, plus its ID.
 * <p>
 * Used by {@link SecurityAspect} to pass both states to
 * {@link PolicyRule} implementations for before/after comparisons.
 *
 * @param <E> entity type (old state)
 * @param <D> DTO type (new state)
 */
public record CompositeResource<E, D>(
        E oldState,
        D newState,
        UUID id
) {
}
