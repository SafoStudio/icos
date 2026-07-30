package com.safo.icos.modules.talent.onboarding.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OnboardingTemplateTest {

  @Test
  void shouldCreateTemplate() {
    UUID companyId = UUID.randomUUID();

    OnboardingTemplate template = OnboardingTemplate.create(companyId, "Standard Onboarding", "Default template");

    assertThat(template.getId()).isNotNull();
    assertThat(template.getCompanyId()).isEqualTo(companyId);
    assertThat(template.getName()).isEqualTo("Standard Onboarding");
    assertThat(template.getDescription()).isEqualTo("Default template");
    assertThat(template.getCreatedAt()).isNotNull();
    assertThat(template.getUpdatedAt()).isNotNull();
  }
}