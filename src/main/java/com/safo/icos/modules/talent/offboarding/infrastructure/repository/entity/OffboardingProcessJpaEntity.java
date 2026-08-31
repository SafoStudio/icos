package com.safo.icos.modules.talent.offboarding.infrastructure.repository.entity;

import com.safo.icos.modules.talent.offboarding.domain.model.enums.OffboardingProcessStatus;
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
@Table(name = "offboarding_processes")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OffboardingProcessJpaEntity {
  @Id
  private UUID id;

  @Column(name = "membership_id", nullable = false)
  private UUID membershipId;

  @Column(name = "template_id")
  private UUID templateId;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private OffboardingProcessStatus status;

  @Column(name = "start_date", nullable = false)
  private LocalDate startDate;

  @Column(name = "last_working_day", nullable = false)
  private LocalDate lastWorkingDay;

  @Column(name = "reason")
  private String reason;

  @Column(name = "initiated_by")
  private String initiatedBy;

  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;
}