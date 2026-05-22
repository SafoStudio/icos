package com.safo.icos.modules.onboarding.infrastructure.mapper;

import com.safo.icos.modules.onboarding.domain.model.OnboardingProcess;
import com.safo.icos.modules.onboarding.domain.model.OnboardingTask;
import com.safo.icos.modules.onboarding.domain.model.OnboardingTaskTemplate;
import com.safo.icos.modules.onboarding.domain.model.OnboardingTemplate;
import com.safo.icos.modules.onboarding.infrastructure.repository.entity.OnboardingProcessJpaEntity;
import com.safo.icos.modules.onboarding.infrastructure.repository.entity.OnboardingTaskJpaEntity;
import com.safo.icos.modules.onboarding.infrastructure.repository.entity.OnboardingTaskTemplateJpaEntity;
import com.safo.icos.modules.onboarding.infrastructure.repository.entity.OnboardingTemplateJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class OnboardingMapper {

    public OnboardingProcess toDomain(OnboardingProcessJpaEntity entity) {
        if (entity == null) return null;
        return OnboardingProcess.builder()
                .id(entity.getId())
                .membershipId(entity.getMembershipId())
                .templateId(entity.getTemplateId())
                .status(entity.getStatus())
                .startDate(entity.getStartDate())
                .targetCompletionDate(entity.getTargetCompletionDate())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public OnboardingProcessJpaEntity toEntity(OnboardingProcess domain) {
        if (domain == null) return null;
        return OnboardingProcessJpaEntity.builder()
                .id(domain.getId())
                .membershipId(domain.getMembershipId())
                .templateId(domain.getTemplateId())
                .status(domain.getStatus())
                .startDate(domain.getStartDate())
                .targetCompletionDate(domain.getTargetCompletionDate())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public OnboardingTask toDomain(OnboardingTaskJpaEntity entity) {
        if (entity == null) return null;
        return OnboardingTask.builder()
                .id(entity.getId())
                .processId(entity.getProcessId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .assigneeId(entity.getAssigneeId())
                .status(entity.getStatus())
                .dueDate(entity.getDueDate())
                .completedAt(entity.getCompletedAt())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public OnboardingTaskJpaEntity toEntity(OnboardingTask domain) {
        if (domain == null) return null;
        return OnboardingTaskJpaEntity.builder()
                .id(domain.getId())
                .processId(domain.getProcessId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .assigneeId(domain.getAssigneeId())
                .status(domain.getStatus())
                .dueDate(domain.getDueDate())
                .completedAt(domain.getCompletedAt())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public OnboardingTemplate toDomain(OnboardingTemplateJpaEntity entity) {
        if (entity == null) return null;
        return OnboardingTemplate.builder()
                .id(entity.getId())
                .companyId(entity.getCompanyId())
                .name(entity.getName())
                .description(entity.getDescription())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public OnboardingTemplateJpaEntity toEntity(OnboardingTemplate domain) {
        if (domain == null) return null;
        return OnboardingTemplateJpaEntity.builder()
                .id(domain.getId())
                .companyId(domain.getCompanyId())
                .name(domain.getName())
                .description(domain.getDescription())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }

    public OnboardingTaskTemplate toDomain(OnboardingTaskTemplateJpaEntity entity) {
        if (entity == null) return null;
        return OnboardingTaskTemplate.builder()
                .id(entity.getId())
                .templateId(entity.getTemplateId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .daysFromStart(entity.getDaysFromStart())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public OnboardingTaskTemplateJpaEntity toEntity(OnboardingTaskTemplate domain) {
        if (domain == null) return null;
        return OnboardingTaskTemplateJpaEntity.builder()
                .id(domain.getId())
                .templateId(domain.getTemplateId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .daysFromStart(domain.getDaysFromStart())
                .createdAt(domain.getCreatedAt())
                .updatedAt(domain.getUpdatedAt())
                .build();
    }
}
