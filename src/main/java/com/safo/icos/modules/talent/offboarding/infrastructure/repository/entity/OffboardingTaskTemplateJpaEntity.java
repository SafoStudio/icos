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
@Table(name = "offboarding_task_templates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OffboardingTaskTemplateJpaEntity {
  @Id
  private UUID id;

  @Column(name = "template_id", nullable = false)
  private UUID templateId;

  @Column(nullable = false)
  private String title;

  @Column(length = 1000)
  private String description;

  @Column(name = "days_from_start", nullable = false)
  private Integer daysFromStart;

  @Column(name = "assignee_role", nullable = false)
  private String assigneeRole;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}