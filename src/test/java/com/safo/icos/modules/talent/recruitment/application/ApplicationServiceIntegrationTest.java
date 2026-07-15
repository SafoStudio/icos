package com.safo.icos.modules.talent.recruitment.application;

import com.safo.icos.modules.talent.recruitment.application.dto.ApplicationDto;
import com.safo.icos.modules.talent.recruitment.application.dto.CreateVacancyRequest;
import com.safo.icos.modules.talent.recruitment.application.dto.KanbanBoardDto;
import com.safo.icos.modules.talent.recruitment.application.dto.KanbanColumnDto;
import com.safo.icos.modules.talent.recruitment.application.dto.VacancyDto;
import com.safo.icos.modules.talent.recruitment.domain.model.Application;
import com.safo.icos.modules.talent.recruitment.domain.model.Candidate;
import com.safo.icos.modules.talent.recruitment.domain.model.enums.ApplicationStatus;
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

@SpringBootTest
@Transactional
class ApplicationServiceIntegrationTest {

  @Autowired private VacancyService vacancyService;
  @Autowired private ApplicationService applicationService;
  @Autowired private CandidateService candidateService;

  private static final UUID COMPANY_ID = UUID.fromString("e4b10b0e-3fa1-4a4e-98ee-5fb9d2a3ea61");
  private static final UUID DEPARTMENT_ID = UUID.fromString("a0f7e1b5-ec6e-4b68-b78f-ef816a3a936a");
  private static final UUID POSITION_ID = UUID.fromString("d608e5c2-f1df-4b5b-8e1f-7b0b3d68fb9d");
  private static final UUID JOHN_DOE_ID = UUID.fromString("7a67f6e0-2f98-4680-bc9b-efb2b7ea672a");

  private UUID vacancyId;
  private UUID candidateId;

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

    CreateVacancyRequest request = new CreateVacancyRequest();
    request.setCompanyId(COMPANY_ID);
    request.setDepartmentId(DEPARTMENT_ID);
    request.setPositionId(POSITION_ID);
    request.setTitle("Software Engineer");
    request.setDescription("Backend developer");
    request.setRequirements("Java, Spring");
    VacancyDto vacancy = vacancyService.createVacancy(request);
    vacancyId = vacancy.getId();

    Candidate candidate = candidateService.createCandidate(
            "Alice", "Johnson", "alice@example.com",
            "+1234567890", "LinkedIn", "resume.pdf");
    candidateId = candidate.getId();
  }

  @AfterEach
  void tearDown() {
    TenantContext.clear();
    SecurityContextHolder.clearContext();
  }

  @Test
  void shouldCreateApplicationAndShowInKanbanBoard() {
    ApplicationDto application = applicationService.createApplication(candidateId, vacancyId);

    assertThat(application.getId()).isNotNull();
    assertThat(application.getCandidateId()).isEqualTo(candidateId);
    assertThat(application.getVacancyId()).isEqualTo(vacancyId);
    assertThat(application.getStatus()).isEqualTo(ApplicationStatus.ACTIVE);

    KanbanBoardDto board = applicationService.getKanbanBoard(vacancyId);

    assertThat(board.getVacancyId()).isEqualTo(vacancyId);
    assertThat(board.getColumns()).hasSize(5);

    KanbanColumnDto appliedColumn = board.getColumns().getFirst();
    assertThat(appliedColumn.getStage().getName()).isEqualTo("Applied");
    assertThat(appliedColumn.getApplications()).hasSize(1);
    assertThat(appliedColumn.getApplications().getFirst().getId()).isEqualTo(application.getId());

    // Other columns should be empty
    for (int i = 1; i < board.getColumns().size(); i++) {
      assertThat(board.getColumns().get(i).getApplications()).isEmpty();
    }
  }

  @Test
  void shouldMoveApplicationToNextStage() {
    ApplicationDto application = applicationService.createApplication(candidateId, vacancyId);

    KanbanBoardDto board = applicationService.getKanbanBoard(vacancyId);
    UUID nextStageId = board.getColumns().get(1).getStage().getId();
    String nextStageName = board.getColumns().get(1).getStage().getName();

    applicationService.moveToStage(application.getId(), nextStageId);

    KanbanBoardDto updatedBoard = applicationService.getKanbanBoard(vacancyId);

    // First column (Applied) should be empty
    assertThat(updatedBoard.getColumns().get(0).getApplications()).isEmpty();

    // Second column should have the application
    KanbanColumnDto newColumn = updatedBoard.getColumns().get(1);
    assertThat(newColumn.getStage().getName()).isEqualTo(nextStageName);
    assertThat(newColumn.getApplications()).hasSize(1);
    assertThat(newColumn.getApplications().getFirst().getId()).isEqualTo(application.getId());
  }

  @Test
  void shouldCreateMultipleApplicationsInSameColumn() {
    Candidate secondCandidate = candidateService.createCandidate(
            "Bob", "Williams", "bob@example.com",
            "+9876543210", "Referral", "resume2.pdf");

    ApplicationDto app1 = applicationService.createApplication(candidateId, vacancyId);
    ApplicationDto app2 = applicationService.createApplication(secondCandidate.getId(), vacancyId);

    KanbanBoardDto board = applicationService.getKanbanBoard(vacancyId);

    assertThat(board.getColumns().getFirst().getApplications()).hasSize(2);
    assertThat(board.getColumns().getFirst().getApplications())
            .extracting(Application::getId)
            .containsExactlyInAnyOrder(app1.getId(), app2.getId());
  }
}
