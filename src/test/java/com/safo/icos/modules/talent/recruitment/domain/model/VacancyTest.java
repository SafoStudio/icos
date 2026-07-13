package com.safo.icos.modules.talent.recruitment.domain.model;

import com.safo.icos.modules.talent.recruitment.domain.model.enums.VacancyStatus;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class VacancyTest {

  @Test
  void shouldCreateVacancyInDraftStatus() {
    UUID companyId = UUID.randomUUID();
    UUID departmentId = UUID.randomUUID();
    UUID positionId = UUID.randomUUID();
    UUID createdBy = UUID.randomUUID();

    Vacancy vacancy = Vacancy.create(companyId, departmentId, positionId,
            "Java Developer", "Backend position", "Java, Spring", createdBy);

    assertThat(vacancy.getId()).isNotNull();
    assertThat(vacancy.getCompanyId()).isEqualTo(companyId);
    assertThat(vacancy.getDepartmentId()).isEqualTo(departmentId);
    assertThat(vacancy.getPositionId()).isEqualTo(positionId);
    assertThat(vacancy.getTitle()).isEqualTo("Java Developer");
    assertThat(vacancy.getStatus()).isEqualTo(VacancyStatus.DRAFT);
    assertThat(vacancy.getCreatedBy()).isEqualTo(createdBy);
    assertThat(vacancy.getCreatedAt()).isNotNull();
  }
}