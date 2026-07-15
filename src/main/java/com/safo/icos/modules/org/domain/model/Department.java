package com.safo.icos.modules.org.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a department within a company.
 */
@Getter
@Builder
public class Department {
  private final UUID id;
  private final UUID companyId;
  private final LocalDateTime createdAt;
  private String name;
  private String description;
  private LocalDateTime updatedAt;

  /**
   * Creates a new department.
   *
   * @param companyId   the owning company
   * @param name        department name
   * @param description optional description
   * @return a newly created {@code Department}
   */
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

  /**
   * Updates the department name and/or description.
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
