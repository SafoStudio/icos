package com.safo.icos.modules.org.domain.model;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a company (tenant).
 */
@Getter
@Builder
public class Company {
  private final UUID id;
  private final String name;
  private final String taxId;
  /** Version of the company's recruitment policy. */
  private final Integer policyVersion;
  private final LocalDateTime createdAt;
  private final LocalDateTime updatedAt;

  /**
   * Creates a new company with default policy version 1.
   *
   * @param name  company name
   * @param taxId tax identifier (may be {@code null})
   * @return a newly created {@code Company}
   */
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
