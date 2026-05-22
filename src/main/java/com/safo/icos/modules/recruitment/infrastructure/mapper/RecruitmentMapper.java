package com.safo.icos.modules.recruitment.infrastructure.mapper;

import com.safo.icos.modules.recruitment.domain.model.Application;
import com.safo.icos.modules.recruitment.domain.model.Candidate;
import com.safo.icos.modules.recruitment.domain.model.Vacancy;
import com.safo.icos.modules.recruitment.domain.model.VacancyStage;
import com.safo.icos.modules.recruitment.infrastructure.repository.entity.ApplicationJpaEntity;
import com.safo.icos.modules.recruitment.infrastructure.repository.entity.CandidateJpaEntity;
import com.safo.icos.modules.recruitment.infrastructure.repository.entity.VacancyJpaEntity;
import com.safo.icos.modules.recruitment.infrastructure.repository.entity.VacancyStageJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class RecruitmentMapper {

    public Vacancy toDomain(VacancyJpaEntity entity) {
        if (entity == null) return null;
        return Vacancy.builder()
                .id(entity.getId())
                .companyId(entity.getCompanyId())
                .departmentId(entity.getDepartmentId())
                .positionId(entity.getPositionId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .requirements(entity.getRequirements())
                .status(entity.getStatus())
                .createdBy(entity.getCreatedBy())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public VacancyJpaEntity toEntity(Vacancy domain) {
        if (domain == null) return null;
        return VacancyJpaEntity.builder()
                .id(domain.getId())
                .companyId(domain.getCompanyId())
                .departmentId(domain.getDepartmentId())
                .positionId(domain.getPositionId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .requirements(domain.getRequirements())
                .status(domain.getStatus())
                .createdBy(domain.getCreatedBy())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Candidate toDomain(CandidateJpaEntity entity) {
        if (entity == null) return null;
        return Candidate.builder()
                .id(entity.getId())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .source(entity.getSource())
                .resumeUrl(entity.getResumeUrl())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public CandidateJpaEntity toEntity(Candidate domain) {
        if (domain == null) return null;
        return CandidateJpaEntity.builder()
                .id(domain.getId())
                .firstName(domain.getFirstName())
                .lastName(domain.getLastName())
                .email(domain.getEmail())
                .phone(domain.getPhone())
                .source(domain.getSource())
                .resumeUrl(domain.getResumeUrl())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public Application toDomain(ApplicationJpaEntity entity) {
        if (entity == null) return null;
        return Application.builder()
                .id(entity.getId())
                .candidateId(entity.getCandidateId())
                .vacancyId(entity.getVacancyId())
                .currentStageId(entity.getCurrentStageId())
                .status(entity.getStatus())
                .rejectionReason(entity.getRejectionReason())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ApplicationJpaEntity toEntity(Application domain) {
        if (domain == null) return null;
        return ApplicationJpaEntity.builder()
                .id(domain.getId())
                .candidateId(domain.getCandidateId())
                .vacancyId(domain.getVacancyId())
                .currentStageId(domain.getCurrentStageId())
                .status(domain.getStatus())
                .rejectionReason(domain.getRejectionReason())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public VacancyStage toDomain(VacancyStageJpaEntity entity) {
        if (entity == null) return null;
        return VacancyStage.builder()
                .id(entity.getId())
                .vacancyId(entity.getVacancyId())
                .name(entity.getName())
                .type(entity.getType())
                .orderIndex(entity.getOrderIndex())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public VacancyStageJpaEntity toEntity(VacancyStage domain) {
        if (domain == null) return null;
        return VacancyStageJpaEntity.builder()
                .id(domain.getId())
                .vacancyId(domain.getVacancyId())
                .name(domain.getName())
                .type(domain.getType())
                .orderIndex(domain.getOrderIndex())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
