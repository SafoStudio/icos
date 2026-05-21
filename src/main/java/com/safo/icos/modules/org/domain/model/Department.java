package com.safo.icos.modules.org.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Department {
  private final UUID id;
  private final UUID companyId;
  private final LocalDateTime createdAt;
  private String name;
  private String description;
  private LocalDateTime updatedAt;

  public static Department create(UUID companyId, String name, String description) {
    return Department.builder()
            .id(UUID.randomUUID())
            .companyId(companyId)
            .name(name)
            .description(description)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
  }

  public void update(String name, String description) {
    if (name != null) this.name = name;
    if (description != null) this.description = description;
    this.updatedAt = LocalDateTime.now();
  }
}
