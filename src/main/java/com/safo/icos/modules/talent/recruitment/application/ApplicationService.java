package com.safo.icos.modules.recruitment.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.recruitment.api.constant.RecruitmentErrorCode;
import com.safo.icos.modules.recruitment.application.dto.ApplicationDto;
import com.safo.icos.modules.recruitment.application.dto.KanbanBoardDto;
import com.safo.icos.modules.recruitment.application.dto.KanbanColumnDto;
import com.safo.icos.modules.recruitment.api.event.CandidateHiredEvent;
import com.safo.icos.modules.recruitment.api.event.CandidateStageChangedEvent;
import com.safo.icos.modules.recruitment.domain.model.Application;
import com.safo.icos.modules.recruitment.domain.model.enums.ApplicationStatus;
import com.safo.icos.modules.recruitment.domain.model.Vacancy;
import com.safo.icos.modules.recruitment.domain.model.VacancyStage;
import com.safo.icos.modules.recruitment.domain.repository.ApplicationRepository;
import com.safo.icos.modules.recruitment.domain.repository.VacancyRepository;
import com.safo.icos.modules.recruitment.infrastructure.repository.jpa.VacancyStageJpaRepository;
import com.safo.icos.modules.recruitment.infrastructure.mapper.RecruitmentMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public ApplicationDto createApplication(UUID candidateId, UUID vacancyId) {
        // Find initial stage for the vacancy
        VacancyStage initialStage = vacancyStageJpaRepository.findByVacancyIdOrderByOrderIndexAsc(vacancyId)
                .stream()
                .findFirst()
                .map(mapper::toDomain)
                .orElseThrow(() -> new BusinessException("No stages defined for vacancy", RecruitmentErrorCode.APPLICATION_NOT_FOUND));

        Application application = Application.create(candidateId, vacancyId, initialStage.getId());
        Application saved = applicationRepository.save(application);

        return toDto(saved);
    }

    public KanbanBoardDto getKanbanBoard(UUID vacancyId) {
        List<VacancyStage> stages = vacancyStageJpaRepository.findByVacancyIdOrderByOrderIndexAsc(vacancyId)
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

    public ApplicationDto getApplication(UUID applicationId) {
        return applicationRepository.findById(applicationId)
                .map(this::toDto)
                .orElseThrow(() -> new BusinessException(APPLICATION_NOT_FOUND_MESSAGE, RecruitmentErrorCode.APPLICATION_NOT_FOUND));
    }

    public void moveToStage(UUID applicationId, UUID stageId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(APPLICATION_NOT_FOUND_MESSAGE, RecruitmentErrorCode.APPLICATION_NOT_FOUND));

        // Logic to validate transition could be added here or in domain service

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

        // Publish event for notifications
        // In a real scenario, we'd get the actual user ID of the recruiter/manager
        eventPublisher.publishEvent(new CandidateStageChangedEvent(
                this,
                application.getCandidateId(),
                application.getId(),
                stageId,
                application.getCandidateId() // Just as an example, notify the candidate
        ));
    }

    public void hireCandidate(UUID applicationId, UUID userId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException(APPLICATION_NOT_FOUND_MESSAGE, RecruitmentErrorCode.APPLICATION_NOT_FOUND));

        Vacancy vacancy = vacancyRepository.findById(application.getVacancyId())
                .orElseThrow(() -> new BusinessException("Vacancy not found", RecruitmentErrorCode.APPLICATION_NOT_FOUND));

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

        // Publish event for Onboarding module
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
