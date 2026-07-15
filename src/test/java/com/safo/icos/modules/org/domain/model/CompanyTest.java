package com.safo.icos.modules.org.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompanyTest {

  @Test
  void shouldCreateCompanyWithDefaultPolicyVersion() {
    Company company = Company.create("TestCo", "123456789");

    assertThat(company.getId()).isNotNull();
    assertThat(company.getName()).isEqualTo("TestCo");
    assertThat(company.getTaxId()).isEqualTo("123456789");
    assertThat(company.getPolicyVersion()).isEqualTo(1);
    assertThat(company.getCreatedAt()).isNotNull();
    assertThat(company.getUpdatedAt()).isNotNull();
  }

  @Test
  void shouldAllowNullTaxId() {
    Company company = Company.create("SoloCo", null);

    assertThat(company.getTaxId()).isNull();
  }
}
