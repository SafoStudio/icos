package com.safo.icos.modules.org.infrastructure.repository;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "companies")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyJpaEntity {
  @Id
  private UUID id;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "tax_id", unique = true)
  private String taxId;

  @Column(name = "policy_version", nullable = false)
  private Integer policyVersion;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}
