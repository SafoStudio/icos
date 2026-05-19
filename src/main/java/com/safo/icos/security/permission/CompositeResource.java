package com.safo.icos.security.permission;

import java.util.UUID;

public record CompositeResource<E, D>(
    E oldState,
    D newState,
    UUID id
) {}
