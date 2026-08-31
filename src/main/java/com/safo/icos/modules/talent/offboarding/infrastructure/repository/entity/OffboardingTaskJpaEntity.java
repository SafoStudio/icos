package com.safo.icos.modules.talent.offboarding.infrastructure.repository.entity;

import com.safo.icos.modules.talent.offboarding.domain.model.enums.OffboardingTaskStatus;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "offboarding_tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OffboardingTaskJpaEntity {
  @Id
  private UUID id;

  @Column(name = "process_id", nullable = false)
  private UUID processId;

  @Column(nullable = false)
  private String title;

  @Column(length = 1000)
  private String description;

  @Column(name = "assignee_id", nullable = false)
  private UUID assigneeId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OffboardingTaskStatus status;

  @Column(name = "due_date")
  private LocalDate dueDate;

  @Column(name = "completed_at")
  private LocalDateTime completedAt;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}