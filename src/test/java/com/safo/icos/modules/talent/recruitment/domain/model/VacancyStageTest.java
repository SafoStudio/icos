package com.safo.icos.modules.talent.recruitment.domain.model;

import com.safo.icos.modules.talent.recruitment.domain.model.enums.PipelineStageType;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VacancyStageTest {

  @Test
  void shouldCreateStage() {
    UUID vacancyId = UUID.randomUUID();

    VacancyStage stage = VacancyStage.create(vacancyId, "Technical Interview",
            PipelineStageType.TECHNICAL, 2);

    assertThat(stage.getId()).isNotNull();
    assertThat(stage.getVacancyId()).isEqualTo(vacancyId);
    assertThat(stage.getName()).isEqualTo("Technical Interview");
    assertThat(stage.getType()).isEqualTo(PipelineStageType.TECHNICAL);
    assertThat(stage.getOrderIndex()).isEqualTo(2);
    assertThat(stage.getCreatedAt()).isNotNull();
  }
}
