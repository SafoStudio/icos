package com.safo.icos.modules.talent.offboarding.infrastructure.repository.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "offboarding_templates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OffboardingTemplateJpaEntity {
  @Id
  private UUID id;

  @Column(name = "company_id", nullable = false)
  private UUID companyId;

  @Column(nullable = false)
  private String name;

  @Column(length = 1000)
  private String description;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}