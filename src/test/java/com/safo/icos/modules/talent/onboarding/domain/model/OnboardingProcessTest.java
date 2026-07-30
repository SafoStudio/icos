package com.safo.icos.modules.talent.onboarding.domain.model;

import com.safo.icos.modules.talent.onboarding.domain.model.enums.OnboardingProcessStatus;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OnboardingProcessTest {

  @Test
  void shouldCreateProcessWithDefaults() {
    UUID membershipId = UUID.randomUUID();
    UUID templateId = UUID.randomUUID();
    LocalDate startDate = LocalDate.of(2026, 7, 1);

    OnboardingProcess process = OnboardingProcess.create(membershipId, templateId, startDate);

    assertThat(process.getId()).isNotNull();
    assertThat(process.getMembershipId()).isEqualTo(membershipId);
    assertThat(process.getTemplateId()).isEqualTo(templateId);
    assertThat(process.getStatus()).isEqualTo(OnboardingProcessStatus.NOT_STARTED);
    assertThat(process.getStartDate()).isEqualTo(startDate);
    assertThat(process.getTargetCompletionDate()).isEqualTo(startDate.plusWeeks(2));
    assertThat(process.getCreatedAt()).isNotNull();
  }
}