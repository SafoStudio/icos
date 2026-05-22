package com.safo.icos.modules.recruitment.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.common.constant.ErrorCode;
import com.safo.icos.modules.recruitment.application.dto.ApplicationDto;
import com.safo.icos.modules.recruitment.domain.model.Application;
import com.safo.icos.modules.recruitment.domain.model.VacancyStage;
import com.safo.icos.modules.recruitment.domain.repository.ApplicationRepository;
import com.safo.icos.modules.recruitment.infrastructure.repository.VacancyStageJpaRepository;
import com.safo.icos.modules.recruitment.infrastructure.mapper.RecruitmentMapper;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ApplicationService {
    private final ApplicationRepository applicationRepository;
    private final VacancyStageJpaRepository vacancyStageJpaRepository;
    private final RecruitmentMapper mapper;

    public ApplicationDto createApplication(UUID candidateId, UUID vacancyId) {
        // Find initial stage for the vacancy
        VacancyStage initialStage = vacancyStageJpaRepository.findByVacancyIdOrderByOrderIndexAsc(vacancyId)
                .stream()
                .findFirst()
                .map(mapper::toDomain)
                .orElseThrow(() -> new BusinessException("No stages defined for vacancy", ErrorCode.NOT_FOUND));

        Application application = Application.create(candidateId, vacancyId, initialStage.getId());
        Application saved = applicationRepository.save(application);

        return toDto(saved);
    }

    public List<ApplicationDto> getApplicationsByVacancy(UUID vacancyId) {
        return applicationRepository.findByVacancyId(vacancyId).stream()
                .map(this::toDto)
                .toList();
    }

    public ApplicationDto getApplication(UUID applicationId) {
        return applicationRepository.findById(applicationId)
                .map(this::toDto)
                .orElseThrow(() -> new BusinessException("Application not found", ErrorCode.NOT_FOUND));
    }

    public void moveToStage(UUID applicationId, UUID stageId) {
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new BusinessException("Application not found", ErrorCode.NOT_FOUND));

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
