package com.safo.icos.modules.talent.offboarding.presentation;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.identity.auth.api.constant.AuthErrorCode;
import com.safo.icos.modules.talent.offboarding.api.dto.OffboardingProcessDto;
import com.safo.icos.modules.talent.offboarding.api.dto.OffboardingTaskDto;
import com.safo.icos.modules.talent.offboarding.api.dto.OffboardingTemplateDto;
import com.safo.icos.modules.talent.offboarding.api.dto.OffboardingTaskTemplateDto;
import com.safo.icos.modules.talent.offboarding.application.OffboardingService;
import com.safo.icos.modules.talent.offboarding.domain.model.enums.OffboardingProcessStatus;
import com.safo.icos.modules.talent.offboarding.domain.repository.OffboardingProcessRepository;
import com.safo.icos.modules.talent.offboarding.domain.repository.OffboardingTaskRepository;
import com.safo.icos.modules.talent.offboarding.domain.repository.OffboardingTemplateRepository;
import com.safo.icos.modules.talent.offboarding.infrastructure.mapper.OffboardingMapper;
import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.RequirePermission;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class OffboardingGraphQLResolver {

  private final OffboardingService offboardingService;
  private final OffboardingProcessRepository processRepository;
  private final OffboardingTemplateRepository templateRepository;
  private final OffboardingTaskRepository taskRepository;
  private final OffboardingMapper mapper;

  @QueryMapping
  @RequirePermission("offboarding:read")
  public OffboardingProcessDto offboardingProcess(@Argument UUID id) {
    return mapper.toDto(offboardingService.getProcess(id));
  }

  @QueryMapping
  @RequirePermission("offboarding:read")
  public OffboardingProcessDto offboardingProcessByMembership(@Argument UUID membershipId) {
    return mapper.toDto(offboardingService.getProcessByMembershipId(membershipId));
  }

  @QueryMapping
  @RequirePermission("offboarding:read")
  public List<OffboardingProcessDto> offboardingProcessesByCompany(@Argument UUID companyId) {
    return offboardingService.getProcessesByCompany(companyId).stream()
            .map(mapper::toDto)
            .toList();
  }

  @QueryMapping
  @RequirePermission("offboarding:task.read")
  public List<OffboardingTaskDto> myOffboardingTasks() {
    IcosUserPrincipal principal = getPrincipal();
    return offboardingService.getTasksByAssignee(principal.getUserId()).stream()
            .map(mapper::toDto)
            .toList();
  }

  @QueryMapping
  @RequirePermission("offboarding:read")
  public List<OffboardingTemplateDto> offboardingTemplates(@Argument UUID companyId) {
    return templateRepository.findByCompanyId(companyId).stream()
            .map(mapper::toDto)
            .toList();
  }

  @QueryMapping
  @RequirePermission("offboarding:task.read")
  public OffboardingTaskDto offboardingTask(@Argument UUID taskId) {
    return mapper.toDto(offboardingService.getTask(taskId));
  }

  @MutationMapping
  @RequirePermission("offboarding:manage")
  public OffboardingTemplateDto createOffboardingTemplate(@Argument CreateOffboardingTemplateInput input) {
    return mapper.toDto(offboardingService.createTemplate(
            input.companyId(), input.name(), input.description()));
  }

  @MutationMapping
  @RequirePermission("offboarding:manage")
  public OffboardingTaskTemplateDto addTaskToOffboardingTemplate(@Argument AddOffboardingTaskTemplateInput input) {
    var taskTemplate = offboardingService.addTaskToTemplate(
            input.templateId(),
            input.title(),
            input.description(),
            input.daysFromStart(),
            input.assigneeRole()
    );
    return mapper.toDto(taskTemplate);
  }

  @MutationMapping
  @RequirePermission("offboarding:manage")
  public OffboardingProcessDto startOffboarding(@Argument StartOffboardingInput input) {
    return mapper.toDto(offboardingService.startOffboarding(
            input.membershipId(),
            input.templateId(),
            LocalDate.parse(input.lastWorkingDay()),
            input.reason(),
            input.initiatedBy()
    ));
  }

  @MutationMapping
  @RequirePermission("offboarding:manage")
  public OffboardingProcessDto approveOffboardingProcess(@Argument UUID processId) {
    return mapper.toDto(offboardingService.approveProcess(processId));
  }

  @MutationMapping
  @RequirePermission("offboarding:manage")
  public OffboardingProcessDto transitionOffboardingProcess(@Argument TransitionOffboardingProcessInput input) {
    return mapper.toDto(offboardingService.transitionProcess(
            input.processId(),
            OffboardingProcessStatus.valueOf(input.status())
    ));
  }

  @MutationMapping
  @RequirePermission("offboarding:manage")
  public OffboardingProcessDto signOffOffboardingProcess(@Argument UUID processId) {
    return mapper.toDto(offboardingService.signOffProcess(processId));
  }

  @MutationMapping
  @RequirePermission("offboarding:manage")
  public Boolean cancelOffboardingProcess(@Argument UUID processId) {
    offboardingService.cancelOffboarding(processId);
    return true;
  }

  @MutationMapping
  @RequirePermission("offboarding:task.complete")
  public OffboardingTaskDto startOffboardingTask(@Argument UUID taskId) {
    offboardingService.startTask(taskId);
    return mapper.toDto(offboardingService.getTask(taskId));
  }

  @MutationMapping
  @RequirePermission("offboarding:task.complete")
  public OffboardingTaskDto completeOffboardingTask(@Argument UUID taskId) {
    offboardingService.completeTask(taskId);
    return mapper.toDto(offboardingService.getTask(taskId));
  }

  @MutationMapping
  @RequirePermission("offboarding:task.complete")
  public OffboardingTaskDto skipOffboardingTask(@Argument UUID taskId) {
    offboardingService.skipTask(taskId);
    return mapper.toDto(offboardingService.getTask(taskId));
  }

  @MutationMapping
  @RequirePermission("offboarding:task.complete")
  public OffboardingTaskDto blockOffboardingTask(@Argument UUID taskId) {
    offboardingService.blockTask(taskId);
    return mapper.toDto(offboardingService.getTask(taskId));
  }

  @MutationMapping
  @RequirePermission("offboarding:task.complete")
  public OffboardingTaskDto unblockOffboardingTask(@Argument UUID taskId) {
    offboardingService.unblockTask(taskId);
    return mapper.toDto(offboardingService.getTask(taskId));
  }

  @SchemaMapping(typeName = "OffboardingProcess")
  public List<OffboardingTaskDto> tasks(OffboardingProcessDto process) {
    return offboardingService.getProcessTasks(process.getId()).stream()
            .map(mapper::toDto)
            .toList();
  }

  private IcosUserPrincipal getPrincipal() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof IcosUserPrincipal principal)) {
      throw new BusinessException("User not authenticated", AuthErrorCode.TOKEN_NOT_FOUND);
    }
    return principal;
  }

  public record CreateOffboardingTemplateInput(UUID companyId, String name, String description) {}
  public record AddOffboardingTaskTemplateInput(UUID templateId, String title, String description,
                                                Integer daysFromStart, String assigneeRole) {}
  public record StartOffboardingInput(UUID membershipId, UUID templateId, String lastWorkingDay,
                                      String reason, String initiatedBy) {}
  public record TransitionOffboardingProcessInput(UUID processId, String status) {}
}