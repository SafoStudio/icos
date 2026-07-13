package com.safo.icos.modules.talent.recruitment.application;

import com.safo.icos.modules.talent.recruitment.domain.model.Candidate;
import com.safo.icos.modules.talent.recruitment.domain.repository.CandidateRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CandidateServiceTest {

  @Mock private CandidateRepository candidateRepository;

  @InjectMocks
  private CandidateService candidateService;

  @Test
  void shouldCreateCandidate() {
    when(candidateRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    Candidate result = candidateService.createCandidate(
            "John", "Doe", "john@example.com",
            "+380991234567", "LinkedIn", "resume.pdf");

    assertThat(result.getId()).isNotNull();
    assertThat(result.getFirstName()).isEqualTo("John");
    assertThat(result.getLastName()).isEqualTo("Doe");
    assertThat(result.getEmail()).isEqualTo("john@example.com");
    assertThat(result.getPhone()).isEqualTo("+380991234567");
    assertThat(result.getSource()).isEqualTo("LinkedIn");
    assertThat(result.getResumeUrl()).isEqualTo("resume.pdf");
    assertThat(result.getCreatedAt()).isNotNull();
    assertThat(result.getUpdatedAt()).isNotNull();

    verify(candidateRepository).save(any());
  }
}