package com.safo.icos.modules.org.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Department {
    private final UUID id;
    private final UUID companyId;
    private final String name;
    private final UUID managerId;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static Department create(UUID companyId, String name, UUID managerId) {
        return Department.builder()
                .id(UUID.randomUUID())
                .companyId(companyId)
                .name(name)
                .managerId(managerId)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
