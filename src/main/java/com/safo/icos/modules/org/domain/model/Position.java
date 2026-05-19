package com.safo.icos.modules.org.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Position {
    private final UUID id;
    private final UUID companyId;
    private final String name;
    private final String description;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static Position create(UUID companyId, String name, String description) {
        return Position.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .name(name)
                .description(description)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
