package com.safo.icos.modules.talent.recruitment.presentation;

import com.safo.icos.modules.talent.recruitment.api.dto.CandidateDto;
import com.safo.icos.modules.talent.recruitment.application.ApplicationService;
import com.safo.icos.modules.talent.recruitment.application.CandidateService;
import com.safo.icos.modules.talent.recruitment.application.VacancyService;
import com.safo.icos.modules.talent.recruitment.application.dto.ApplicationDto;
import com.safo.icos.modules.talent.recruitment.application.dto.CreateVacancyRequest;
import com.safo.icos.modules.talent.recruitment.application.dto.KanbanBoardDto;
import com.safo.icos.modules.talent.recruitment.application.dto.VacancyDto;
import com.safo.icos.modules.talent.recruitment.domain.model.Candidate;
import com.safo.icos.modules.talent.recruitment.domain.model.enums.VacancyStatus;
import com.safo.icos.modules.talent.recruitment.domain.repository.CandidateRepository;
import com.safo.icos.security.permission.RequirePermission;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class RecruitmentGraphQLResolver {
  private final VacancyService vacancyService;
  private final ApplicationService applicationService;
  private final CandidateService candidateService;
  private final CandidateRepository candidateRepository;

  @QueryMapping
  @RequirePermission("recruitment:read")
  public List<VacancyDto> vacancies(@Argument UUID companyId) {
    return vacancyService.getVacanciesByCompany(companyId);
  }

  @QueryMapping
  @RequirePermission("recruitment:read")
  public VacancyDto vacancy(@Argument UUID id) {
    return vacancyService.getVacancy(id);
  }

  @QueryMapping
  @RequirePermission("application:manage")
  public ApplicationDto application(@Argument UUID id) {
    return applicationService.getApplication(id);
  }

  @QueryMapping
  @RequirePermission("application:manage")
  public KanbanBoardDto kanbanBoard(@Argument UUID vacancyId) {
    return applicationService.getKanbanBoard(vacancyId);
  }

  @SchemaMapping(typeName = "Application")
  public CandidateDto candidate(ApplicationDto application) {
    return candidateService.getCandidate(application.getCandidateId());
  }

  @MutationMapping
  @RequirePermission("recruitment:manage")
  public VacancyDto createVacancy(@Argument CreateVacancyRequest input) {
    return vacancyService.createVacancy(input);
  }

  @MutationMapping
  @RequirePermission("application:manage")
  public ApplicationDto applyToVacancy(@Argument UUID vacancyId, @Argument CandidateInput candidate) {
    Candidate savedCandidate = candidateService.createCandidate(
            candidate.firstName(),
            candidate.lastName(),
            candidate.email(),
            candidate.phone(),
            candidate.source(),
            candidate.resumeUrl()
    );
    return applicationService.createApplication(savedCandidate.getId(), vacancyId);
  }

  @MutationMapping
  @RequirePermission("application:manage")
  public ApplicationDto moveApplication(@Argument UUID applicationId, @Argument UUID stageId) {
    applicationService.moveToStage(applicationId, stageId);
    return applicationService.getApplication(applicationId);
  }

  @MutationMapping
  @RequirePermission("application:manage")
  public ApplicationDto hireCandidate(@Argument UUID applicationId, @Argument UUID userId) {
    applicationService.hireCandidate(applicationId, userId);
    return applicationService.getApplication(applicationId);
  }

  @MutationMapping
  @RequirePermission("recruitment:manage")
  public VacancyDto updateVacancyStatus(@Argument UUID vacancyId, @Argument VacancyStatus status) {
    return vacancyService.updateVacancyStatus(vacancyId, status);
  }

  public record CandidateInput(
          String firstName,
          String lastName,
          String email,
          String phone,
          String source,
          String resumeUrl
  ) {}
}