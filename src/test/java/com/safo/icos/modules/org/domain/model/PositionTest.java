package com.safo.icos.modules.org.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class PositionTest {

  private static final UUID COMPANY_ID = UUID.randomUUID();

  @Test
  void shouldCreatePosition() {
    Position position = Position.create(COMPANY_ID, "Developer", "Backend developer");

    assertThat(position.getId()).isNotNull();
    assertThat(position.getCompanyId()).isEqualTo(COMPANY_ID);
    assertThat(position.getName()).isEqualTo("Developer");
    assertThat(position.getDescription()).isEqualTo("Backend developer");
    assertThat(position.getCreatedAt()).isNotNull();
    assertThat(position.getUpdatedAt()).isNotNull();
  }

  @Test
  void shouldUpdateNameOnly() {
    Position position = Position.create(COMPANY_ID, "Developer", "Old");

    position.update("Senior Developer", null);

    assertThat(position.getName()).isEqualTo("Senior Developer");
    assertThat(position.getDescription()).isEqualTo("Old");
  }

  @Test
  void shouldUpdateDescriptionOnly() {
    Position position = Position.create(COMPANY_ID, "Developer", "Old");

    position.update(null, "Senior backend developer");

    assertThat(position.getName()).isEqualTo("Developer");
    assertThat(position.getDescription()).isEqualTo("Senior backend developer");
  }

  @Test
  void shouldNotChangeFieldsWhenNullPassed() {
    Position position = Position.create(COMPANY_ID, "QA", "Tester");

    position.update(null, null);

    assertThat(position.getName()).isEqualTo("QA");
    assertThat(position.getDescription()).isEqualTo("Tester");
  }
}