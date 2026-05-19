package com.safo.icos.modules.org.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class Company {
    private final UUID id;
    private final String name;
    private final String taxId;
    private final Integer policyVersion;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    public static Company create(String name, String taxId) {
        return Company.builder()
                .id(UUID.randomUUID())
                .name(name)
                .taxId(taxId)
                .policyVersion(1)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
