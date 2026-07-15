package com.safo.icos.modules.org.domain.model;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DepartmentTest {

  private static final UUID COMPANY_ID = UUID.randomUUID();

  @Test
  void shouldCreateDepartment() {
    Department dept = Department.create(COMPANY_ID, "Engineering", "Dev team");

    assertThat(dept.getId()).isNotNull();
    assertThat(dept.getCompanyId()).isEqualTo(COMPANY_ID);
    assertThat(dept.getName()).isEqualTo("Engineering");
    assertThat(dept.getDescription()).isEqualTo("Dev team");
    assertThat(dept.getCreatedAt()).isNotNull();
    assertThat(dept.getUpdatedAt()).isNotNull();
  }

  @Test
  void shouldUpdateNameOnly() {
    Department dept = Department.create(COMPANY_ID, "Engineering", "Old description");

    dept.update("New Engineering", null);

    assertThat(dept.getName()).isEqualTo("New Engineering");
    assertThat(dept.getDescription()).isEqualTo("Old description");
  }

  @Test
  void shouldUpdateDescriptionOnly() {
    Department dept = Department.create(COMPANY_ID, "Engineering", "Old description");

    dept.update(null, "New description");

    assertThat(dept.getName()).isEqualTo("Engineering");
    assertThat(dept.getDescription()).isEqualTo("New description");
  }

  @Test
  void shouldUpdateBothNameAndDescription() {
    Department dept = Department.create(COMPANY_ID, "Engineering", "Old");

    dept.update("QA", "Testing team");

    assertThat(dept.getName()).isEqualTo("QA");
    assertThat(dept.getDescription()).isEqualTo("Testing team");
  }

  @Test
  void shouldNotChangeFieldsWhenNullPassed() {
    Department dept = Department.create(COMPANY_ID, "HR", "Human Resources");

    dept.update(null, null);

    assertThat(dept.getName()).isEqualTo("HR");
    assertThat(dept.getDescription()).isEqualTo("Human Resources");
  }
}
