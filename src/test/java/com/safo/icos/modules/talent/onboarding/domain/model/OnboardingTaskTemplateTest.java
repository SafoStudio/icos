package com.safo.icos.modules.talent.onboarding.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OnboardingTaskTemplateTest {

  @Test
  void shouldCreateTaskTemplate() {
    UUID templateId = UUID.randomUUID();

    OnboardingTaskTemplate taskTemplate = OnboardingTaskTemplate.create(
            templateId, "Submit documents", "Upload ID and diploma", 3);

    assertThat(taskTemplate.getId()).isNotNull();
    assertThat(taskTemplate.getTemplateId()).isEqualTo(templateId);
    assertThat(taskTemplate.getTitle()).isEqualTo("Submit documents");
    assertThat(taskTemplate.getDescription()).isEqualTo("Upload ID and diploma");
    assertThat(taskTemplate.getDaysFromStart()).isEqualTo(3);
    assertThat(taskTemplate.getCreatedAt()).isNotNull();
  }
}