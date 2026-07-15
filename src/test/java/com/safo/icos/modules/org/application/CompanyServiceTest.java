package com.safo.icos.modules.org.application;

import com.safo.icos.modules.org.application.dto.CompanyDto;
import com.safo.icos.modules.org.domain.model.Company;
import com.safo.icos.modules.org.domain.repository.CompanyRepository;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompanyServiceTest {

  @Mock private CompanyRepository companyRepository;

  @InjectMocks
  private CompanyService companyService;

  @Test
  void shouldCreateCompanyInternal() {
    when(companyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

    CompanyDto result = companyService.createCompanyInternal("TestCo", "123456789");

    assertThat(result.getName()).isEqualTo("TestCo");
    assertThat(result.getTaxId()).isEqualTo("123456789");
    assertThat(result.getId()).isNotNull();
    verify(companyRepository).save(any());
  }

  @Test
  void shouldGetCompany() {
    UUID id = UUID.randomUUID();
    Company company = Company.builder()
            .id(id)
            .name("TestCo")
            .taxId("123456789")
            .policyVersion(1)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();

    when(companyRepository.findById(id)).thenReturn(Optional.of(company));

    CompanyDto result = companyService.getCompany(id);

    assertThat(result.getId()).isEqualTo(id);
    assertThat(result.getName()).isEqualTo("TestCo");
  }

  @Test
  void shouldThrowWhenCompanyNotFound() {
    UUID id = UUID.randomUUID();
    when(companyRepository.findById(id)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> companyService.getCompany(id))
            .isInstanceOf(RuntimeException.class)
            .hasMessageContaining("not found");
  }
}
