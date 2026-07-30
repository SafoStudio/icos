package com.safo.icos.modules.talent.onboarding.domain.model;

import com.safo.icos.modules.talent.onboarding.domain.model.enums.OnboardingTaskStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OnboardingTaskTest {

  @Test
  void shouldCreatePendingTask() {
    UUID processId = UUID.randomUUID();
    UUID assigneeId = UUID.randomUUID();
    LocalDate dueDate = LocalDate.of(2026, 7, 15);

    OnboardingTask task = OnboardingTask.create(processId, "Setup email", "Configure email account", assigneeId, dueDate);

    assertThat(task.getId()).isNotNull();
    assertThat(task.getProcessId()).isEqualTo(processId);
    assertThat(task.getTitle()).isEqualTo("Setup email");
    assertThat(task.getDescription()).isEqualTo("Configure email account");
    assertThat(task.getAssigneeId()).isEqualTo(assigneeId);
    assertThat(task.getStatus()).isEqualTo(OnboardingTaskStatus.PENDING);
    assertThat(task.getDueDate()).isEqualTo(dueDate);
    assertThat(task.getCompletedAt()).isNull();
    assertThat(task.getCreatedAt()).isNotNull();
  }
}