package com.safo.icos.modules.talent.recruitment.domain.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CandidateTest {

  @Test
  void shouldCreateCandidate() {
    Candidate candidate = Candidate.create(
            "John", "Doe", "john@example.com",
            "+380991234567", "LinkedIn", "http://files/resume.pdf");

    assertThat(candidate.getId()).isNotNull();
    assertThat(candidate.getFirstName()).isEqualTo("John");
    assertThat(candidate.getLastName()).isEqualTo("Doe");
    assertThat(candidate.getEmail()).isEqualTo("john@example.com");
    assertThat(candidate.getPhone()).isEqualTo("+380991234567");
    assertThat(candidate.getSource()).isEqualTo("LinkedIn");
    assertThat(candidate.getResumeUrl()).isEqualTo("http://files/resume.pdf");
    assertThat(candidate.getCreatedAt()).isNotNull();
    assertThat(candidate.getUpdatedAt()).isNotNull();
  }
}