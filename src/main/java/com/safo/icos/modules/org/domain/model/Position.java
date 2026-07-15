package com.safo.icos.modules.org.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a job position within a company.
 */
@Getter
@Builder
public class Position {
  private final UUID id;
  private final UUID companyId;
  private final LocalDateTime createdAt;
  private String name;
  private String description;
  private LocalDateTime updatedAt;

  /**
   * Creates a new position.
   *
   * @param companyId   the owning company
   * @param name        position name
   * @param description optional description
   * @return a newly created {@code Position}
   */
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

  /**
   * Updates the position name and/or description.
   * {@code null} arguments are ignored.
   *
   * @param name        new name, or {@code null} to keep current
   * @param description new description, or {@code null} to keep current
   */
  public void update(String name, String description) {
    if (name != null) this.name = name;
    if (description != null) this.description = description;
    this.updatedAt = LocalDateTime.now();
  }
}
