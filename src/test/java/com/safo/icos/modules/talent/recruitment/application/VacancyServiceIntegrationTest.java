package com.safo.icos.modules.talent.recruitment.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.talent.recruitment.application.dto.CreateVacancyRequest;
import com.safo.icos.modules.talent.recruitment.application.dto.VacancyDto;
import com.safo.icos.modules.talent.recruitment.domain.model.enums.VacancyStatus;
import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.context.TenantContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class VacancyServiceIntegrationTest {

  @Autowired private VacancyService vacancyService;

  private static final UUID COMPANY_ID = UUID.fromString("e4b10b0e-3fa1-4a4e-98ee-5fb9d2a3ea61");
  private static final UUID DEPARTMENT_ID = UUID.fromString("a0f7e1b5-ec6e-4b68-b78f-ef816a3a936a");
  private static final UUID POSITION_ID = UUID.fromString("d608e5c2-f1df-4b5b-8e1f-7b0b3d68fb9d");
  private static final UUID JOHN_DOE_ID = UUID.fromString("7a67f6e0-2f98-4680-bc9b-efb2b7ea672a");

  @BeforeEach
  void setUp() {
    TenantContext.setTenantId(COMPANY_ID.toString());

    IcosUserPrincipal principal = IcosUserPrincipal.builder()
            .userId(JOHN_DOE_ID)
            .email("john.doe@acme.com")
            .membershipId(UUID.randomUUID())
            .companyId(COMPANY_ID)
            .roleName("ADMIN")
            .authorities(List.of())
            .build();

    SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()));
  }

  @AfterEach
  void tearDown() {
    TenantContext.clear();
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldCreateVacancyWithDefaultPipeline() {
    CreateVacancyRequest request = new CreateVacancyRequest();
    request.setCompanyId(COMPANY_ID);
    request.setDepartmentId(DEPARTMENT_ID);
    request.setPositionId(POSITION_ID);
    request.setTitle("Senior Java Developer");
    request.setDescription("Backend role with Spring expertise");
    request.setRequirements("Java 21, Spring Boot, PostgreSQL");

    VacancyDto result = vacancyService.createVacancy(request);

    assertThat(result.getId()).isNotNull();
    assertThat(result.getTitle()).isEqualTo("Senior Java Developer");
    assertThat(result.getDescription()).isEqualTo("Backend role with Spring expertise");
    assertThat(result.getRequirements()).isEqualTo("Java 21, Spring Boot, PostgreSQL");
    assertThat(result.getCompanyId()).isEqualTo(COMPANY_ID);
    assertThat(result.getDepartmentId()).isEqualTo(DEPARTMENT_ID);
    assertThat(result.getPositionId()).isEqualTo(POSITION_ID);
    assertThat(result.getStatus()).isEqualTo(VacancyStatus.DRAFT);
    assertThat(result.getCreatedAt()).isNotNull();
  }

  @Test
  void shouldGetVacanciesByCompany() {
    CreateVacancyRequest request = new CreateVacancyRequest();
    request.setCompanyId(COMPANY_ID);
    request.setDepartmentId(DEPARTMENT_ID);
    request.setPositionId(POSITION_ID);
    request.setTitle("QA Engineer");
    request.setDescription("Testing role");
    request.setRequirements("JUnit, Selenium");

    VacancyDto created = vacancyService.createVacancy(request);

    List<VacancyDto> vacancies = vacancyService.getVacanciesByCompany(COMPANY_ID);

    assertThat(vacancies).isNotEmpty();
    assertThat(vacancies).extracting(VacancyDto::getId).contains(created.getId());
    assertThat(vacancies).extracting(VacancyDto::getTitle).contains("QA Engineer");
  }

  @Test
  void shouldGetVacancyById() {
    CreateVacancyRequest request = new CreateVacancyRequest();
    request.setCompanyId(COMPANY_ID);
    request.setDepartmentId(DEPARTMENT_ID);
    request.setPositionId(POSITION_ID);
    request.setTitle("DevOps Engineer");
    request.setDescription("Infrastructure role");
    request.setRequirements("Docker, K8s, Terraform");

    VacancyDto created = vacancyService.createVacancy(request);
    VacancyDto found = vacancyService.getVacancy(created.getId());

    assertThat(found.getId()).isEqualTo(created.getId());
    assertThat(found.getTitle()).isEqualTo("DevOps Engineer");
    assertThat(found.getDescription()).isEqualTo("Infrastructure role");
    assertThat(found.getRequirements()).isEqualTo("Docker, K8s, Terraform");
    assertThat(found.getStatus()).isEqualTo(VacancyStatus.DRAFT);
  }

  @Test
  void shouldThrowWhenVacancyNotFound() {
    UUID nonExistentId = UUID.randomUUID();

    assertThatThrownBy(() -> vacancyService.getVacancy(nonExistentId))
            .isInstanceOf(BusinessException.class)
            .hasMessageContaining("not found");
  }
}