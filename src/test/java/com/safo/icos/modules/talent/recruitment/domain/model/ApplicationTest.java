package com.safo.icos.modules.talent.recruitment.domain.model;

import com.safo.icos.modules.talent.recruitment.domain.model.enums.ApplicationStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ApplicationTest {

  @Test
  void shouldCreateActiveApplication() {
    UUID candidateId = UUID.randomUUID();
    UUID vacancyId = UUID.randomUUID();
    UUID stageId = UUID.randomUUID();

    Application application = Application.create(candidateId, vacancyId, stageId);

    assertThat(application.getId()).isNotNull();
    assertThat(application.getCandidateId()).isEqualTo(candidateId);
    assertThat(application.getVacancyId()).isEqualTo(vacancyId);
    assertThat(application.getCurrentStageId()).isEqualTo(stageId);
    assertThat(application.getStatus()).isEqualTo(ApplicationStatus.ACTIVE);
    assertThat(application.getCreatedAt()).isNotNull();
  }
}
