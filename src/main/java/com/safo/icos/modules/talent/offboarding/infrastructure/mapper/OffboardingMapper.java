package com.safo.icos.modules.talent.offboarding.infrastructure.mapper;

import com.safo.icos.modules.talent.offboarding.api.dto.OffboardingProcessDto;
import com.safo.icos.modules.talent.offboarding.api.dto.OffboardingTaskDto;
import com.safo.icos.modules.talent.offboarding.api.dto.OffboardingTemplateDto;
import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingProcess;
import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingTask;
import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingTaskTemplate;
import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingTemplate;
import com.safo.icos.modules.talent.offboarding.infrastructure.repository.entity.OffboardingProcessJpaEntity;
import com.safo.icos.modules.talent.offboarding.infrastructure.repository.entity.OffboardingTaskJpaEntity;
import com.safo.icos.modules.talent.offboarding.infrastructure.repository.entity.OffboardingTaskTemplateJpaEntity;
import com.safo.icos.modules.talent.offboarding.infrastructure.repository.entity.OffboardingTemplateJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class OffboardingMapper {

  // PROCESS

  public OffboardingProcess toDomain(OffboardingProcessJpaEntity entity) {
    if (entity == null) return null;
    return OffboardingProcess.builder()
            .id(entity.getId())
            .membershipId(entity.getMembershipId())
            .templateId(entity.getTemplateId())
            .status(entity.getStatus())
            .startDate(entity.getStartDate())
            .lastWorkingDay(entity.getLastWorkingDay())
            .reason(entity.getReason())
            .initiatedBy(entity.getInitiatedBy())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
  }

  public OffboardingProcessJpaEntity toEntity(OffboardingProcess domain) {
    if (domain == null) return null;
    return OffboardingProcessJpaEntity.builder()
            .id(domain.getId())
            .membershipId(domain.getMembershipId())
            .templateId(domain.getTemplateId())
            .status(domain.getStatus())
            .startDate(domain.getStartDate())
            .lastWorkingDay(domain.getLastWorkingDay())
            .reason(domain.getReason())
            .initiatedBy(domain.getInitiatedBy())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
  }

  // TASK

  public OffboardingTask toDomain(OffboardingTaskJpaEntity entity) {
    if (entity == null) return null;
    return OffboardingTask.builder()
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

  public OffboardingTaskJpaEntity toEntity(OffboardingTask domain) {
    if (domain == null) return null;
    return OffboardingTaskJpaEntity.builder()
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

  // TEMPLATE

  public OffboardingTemplate toDomain(OffboardingTemplateJpaEntity entity) {
    if (entity == null) return null;
    return OffboardingTemplate.builder()
            .id(entity.getId())
            .companyId(entity.getCompanyId())
            .name(entity.getName())
            .description(entity.getDescription())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
  }

  public OffboardingTemplateJpaEntity toEntity(OffboardingTemplate domain) {
    if (domain == null) return null;
    return OffboardingTemplateJpaEntity.builder()
            .id(domain.getId())
            .companyId(domain.getCompanyId())
            .name(domain.getName())
            .description(domain.getDescription())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
  }

  // TASK TEMPLATE

  public OffboardingTaskTemplate toDomain(OffboardingTaskTemplateJpaEntity entity) {
    if (entity == null) return null;
    return OffboardingTaskTemplate.builder()
            .id(entity.getId())
            .templateId(entity.getTemplateId())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .daysFromStart(entity.getDaysFromStart())
            .assigneeRole(entity.getAssigneeRole())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
  }

  public OffboardingTaskTemplateJpaEntity toEntity(OffboardingTaskTemplate domain) {
    if (domain == null) return null;
    return OffboardingTaskTemplateJpaEntity.builder()
            .id(domain.getId())
            .templateId(domain.getTemplateId())
            .title(domain.getTitle())
            .description(domain.getDescription())
            .daysFromStart(domain.getDaysFromStart())
            .assigneeRole(domain.getAssigneeRole())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
  }

  // DTOs

  public OffboardingProcessDto toDto(OffboardingProcess domain) {
    if (domain == null) return null;
    return OffboardingProcessDto.builder()
            .id(domain.getId())
            .membershipId(domain.getMembershipId())
            .status(domain.getStatus().name())
            .startDate(domain.getStartDate())
            .lastWorkingDay(domain.getLastWorkingDay())
            .reason(domain.getReason())
            .build();
  }

  public OffboardingTaskDto toDto(OffboardingTask domain) {
    if (domain == null) return null;
    return OffboardingTaskDto.builder()
            .id(domain.getId())
            .title(domain.getTitle())
            .description(domain.getDescription())
            .status(domain.getStatus().name())
            .dueDate(domain.getDueDate())
            .assigneeId(domain.getAssigneeId())
            .build();
  }

  public OffboardingTemplateDto toDto(OffboardingTemplate domain) {
    if (domain == null) return null;
    return OffboardingTemplateDto.builder()
            .id(domain.getId())
            .companyId(domain.getCompanyId())
            .name(domain.getName())
            .description(domain.getDescription())
            .build();
  }
}