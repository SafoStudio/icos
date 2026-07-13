package com.safo.icos.modules.talent.recruitment.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.talent.recruitment.api.constant.RecruitmentErrorCode;
import com.safo.icos.modules.talent.recruitment.api.event.CandidateHiredEvent;
import com.safo.icos.modules.talent.recruitment.api.event.CandidateStageChangedEvent;
import com.safo.icos.modules.talent.recruitment.application.dto.ApplicationDto;
import com.safo.icos.modules.talent.recruitment.application.dto.KanbanBoardDto;
import com.safo.icos.modules.talent.recruitment.application.dto.KanbanColumnDto;
import com.safo.icos.modules.talent.recruitment.domain.model.Application;
import com.safo.icos.modules.talent.recruitment.domain.model.Vacancy;
import com.safo.icos.modules.talent.recruitment.domain.model.VacancyStage;
import com.safo.icos.modules.talent.recruitment.domain.model.enums.ApplicationStatus;
import com.safo.icos.modules.talent.recruitment.domain.repository.ApplicationRepository;
import com.safo.icos.modules.talent.recruitment.domain.repository.VacancyRepository;
import com.safo.icos.modules.talent.recruitment.infrastructure.mapper.RecruitmentMapper;
import com.safo.icos.modules.talent.recruitment.infrastructure.repository.jpa.VacancyStageJpaRepository;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for managing job applications (candidate submissions to vacancies).
 * Handles the recruitment pipeline: application creation, stage movement,
 * Kanban board projection, and candidate hiring.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {

  private static final String APPLICATION_NOT_FOUND_MESSAGE = "Application not found";

  private final ApplicationRepository applicationRepository;
  private final VacancyRepository vacancyRepository;
  private final VacancyStageJpaRepository vacancyStageJpaRepository;
  private final RecruitmentMapper mapper;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * Submits a new application for a candidate to a vacancy.
   * The application starts at the first stage of the vacancy's pipeline.
   *
   * @param candidateId the candidate's UUID
   * @param vacancyId   the vacancy's UUID
   * @return the created application
   * @throws BusinessException if no stages are defined for the vacancy
   */
  public ApplicationDto createApplication(UUID candidateId, UUID vacancyId) {
    VacancyStage initialStage = vacancyStageJpaRepository
            .findByVacancyIdOrderByOrderIndexAsc(vacancyId)
            .stream()
            .findFirst()
            .map(mapper::toDomain)
            .orElseThrow(() -> new BusinessException(
                    "No stages defined for vacancy",
                    RecruitmentErrorCode.APPLICATION_NOT_FOUND));

    Application application = Application.create(candidateId, vacancyId, initialStage.getId());
    Application saved = applicationRepository.save(application);

    return toDto(saved);
  }

  /**
   * Returns a Kanban board view of all applications for a vacancy,
   * grouped by pipeline stages.
   *
   * @param vacancyId the vacancy's UUID
   * @return the Kanban board with columns and applications
   */
  public KanbanBoardDto getKanbanBoard(UUID vacancyId) {
    List<VacancyStage> stages = vacancyStageJpaRepository
            .findByVacancyIdOrderByOrderIndexAsc(vacancyId)
            .stream()
            .map(mapper::toDomain)
            .toList();

    List<Application> applications = applicationRepository.findByVacancyId(vacancyId);

    List<KanbanColumnDto> columns = stages.stream()
            .map(stage -> KanbanColumnDto.builder()
                    .stage(stage)
                    .applications(applications.stream()
                            .filter(app -> stage.getId().equals(app.getCurrentStageId()))
                            .toList())
                    .build())
            .toList();

    return KanbanBoardDto.builder()
            .vacancyId(vacancyId)
            .columns(columns)
            .build();
  }

  /**
   * Retrieves a single application by ID.
   *
   * @param applicationId the application's UUID
   * @return the application DTO
   * @throws BusinessException if not found
   */
  public ApplicationDto getApplication(UUID applicationId) {
    return applicationRepository.findById(applicationId)
            .map(this::toDto)
            .orElseThrow(() -> new BusinessException(
                    APPLICATION_NOT_FOUND_MESSAGE,
                    RecruitmentErrorCode.APPLICATION_NOT_FOUND));
  }

  /**
   * Moves an application to a different recruitment stage.
   * Publishes a {@link CandidateStageChangedEvent} for notifications.
   *
   * @param applicationId the application's UUID
   * @param stageId       the target stage's UUID
   * @throws BusinessException if the application is not found
   */
  public void moveToStage(UUID applicationId, UUID stageId) {
    Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new BusinessException(
                    APPLICATION_NOT_FOUND_MESSAGE,
                    RecruitmentErrorCode.APPLICATION_NOT_FOUND));

    Application updated = Application.builder()
            .id(application.getId())
            .candidateId(application.getCandidateId())
            .vacancyId(application.getVacancyId())
            .currentStageId(stageId)
            .status(application.getStatus())
            .rejectionReason(application.getRejectionReason())
            .createdAt(application.getCreatedAt())
            .updatedAt(java.time.LocalDateTime.now())
            .build();

    applicationRepository.save(updated);

    eventPublisher.publishEvent(new CandidateStageChangedEvent(
            this,
            application.getCandidateId(),
            application.getId(),
            stageId,
            application.getCandidateId()
    ));
  }

  /**
   * Marks an application as hired and publishes a {@link CandidateHiredEvent}
   * to trigger downstream processes (e.g., onboarding, user creation).
   *
   * @param applicationId the application's UUID
   * @param userId        the newly created user ID for the hired candidate
   * @throws BusinessException if the application or vacancy is not found
   */
  public void hireCandidate(UUID applicationId, UUID userId) {
    Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new BusinessException(
                    APPLICATION_NOT_FOUND_MESSAGE,
                    RecruitmentErrorCode.APPLICATION_NOT_FOUND));

    Vacancy vacancy = vacancyRepository.findById(application.getVacancyId())
            .orElseThrow(() -> new BusinessException(
                    "Vacancy not found",
                    RecruitmentErrorCode.APPLICATION_NOT_FOUND));

    Application updated = Application.builder()
            .id(application.getId())
            .candidateId(application.getCandidateId())
            .vacancyId(application.getVacancyId())
            .currentStageId(application.getCurrentStageId())
            .status(ApplicationStatus.HIRED)
            .rejectionReason(application.getRejectionReason())
            .createdAt(application.getCreatedAt())
            .updatedAt(java.time.LocalDateTime.now())
            .build();

    applicationRepository.save(updated);

    eventPublisher.publishEvent(new CandidateHiredEvent(
            this,
            application.getCandidateId(),
            application.getVacancyId(),
            vacancy.getCompanyId(),
            userId
    ));
  }

  private ApplicationDto toDto(Application application) {
    return ApplicationDto.builder()
            .id(application.getId())
            .candidateId(application.getCandidateId())
            .vacancyId(application.getVacancyId())
            .currentStageId(application.getCurrentStageId())
            .status(application.getStatus())
            .createdAt(application.getCreatedAt())
            .build();
  }
}