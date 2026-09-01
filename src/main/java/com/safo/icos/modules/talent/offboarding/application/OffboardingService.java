package com.safo.icos.modules.talent.offboarding.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.identity.user.application.UserService;
import com.safo.icos.modules.identity.user.api.dto.MembershipDto;
import com.safo.icos.modules.talent.offboarding.api.constant.OffboardingErrorCode;
import com.safo.icos.modules.talent.offboarding.api.event.EmployeeOffboardedEvent;
import com.safo.icos.modules.talent.offboarding.api.event.OffboardingTaskAssignedEvent;
import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingProcess;
import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingTask;
import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingTaskTemplate;
import com.safo.icos.modules.talent.offboarding.domain.model.OffboardingTemplate;
import com.safo.icos.modules.talent.offboarding.domain.model.enums.OffboardingProcessStatus;
import com.safo.icos.modules.talent.offboarding.domain.model.enums.OffboardingTaskStatus;
import com.safo.icos.modules.talent.offboarding.domain.repository.OffboardingProcessRepository;
import com.safo.icos.modules.talent.offboarding.domain.repository.OffboardingTaskRepository;
import com.safo.icos.modules.talent.offboarding.domain.repository.OffboardingTaskTemplateRepository;
import com.safo.icos.modules.talent.offboarding.domain.repository.OffboardingTemplateRepository;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OffboardingService {

  private static final String PROCESS_NOT_FOUND = "Process not found";
  private static final String TASK_NOT_FOUND = "Task not found";
  private static final String MEMBERSHIP_NOT_FOUND = "Membership not found";

  private final OffboardingProcessRepository processRepository;
  private final OffboardingTaskRepository taskRepository;
  private final OffboardingTemplateRepository templateRepository;
  private final OffboardingTaskTemplateRepository taskTemplateRepository;
  private final UserService userService;
  private final ApplicationEventPublisher eventPublisher;

  public OffboardingTemplate createTemplate(UUID companyId, String name, String description) {
    OffboardingTemplate template = OffboardingTemplate.create(companyId, name, description);
    return templateRepository.save(template);
  }

  public void addTaskToTemplate(UUID templateId, String title, String description,
                                Integer daysFromStart, String assigneeRole) {
    templateRepository.findById(templateId)
            .orElseThrow(() -> new BusinessException(
                    "Template not found", OffboardingErrorCode.RESOURCE_NOT_FOUND));

    OffboardingTaskTemplate taskTemplate = OffboardingTaskTemplate.create(
            templateId, title, description, daysFromStart, assigneeRole);
    taskTemplateRepository.save(taskTemplate);
  }

  public OffboardingProcess startOffboarding(
          UUID membershipId,
          UUID templateId,
          LocalDate lastWorkingDay,
          String reason,
          String initiatedBy
  ) {
    log.info("Starting offboarding for membership: {}, lastWorkingDay: {}", membershipId, lastWorkingDay);

    Optional<OffboardingProcess> existingProcess = processRepository.findByMembershipId(membershipId);
    if (existingProcess.isPresent()) {
      OffboardingProcessStatus status = existingProcess.get().getStatus();
      if (status != OffboardingProcessStatus.COMPLETED && status != OffboardingProcessStatus.CANCELLED) {
        throw new BusinessException(
                "Employee already in offboarding process with status: " + status,
                OffboardingErrorCode.EMPLOYEE_ALREADY_OFFBOARDING
        );
      }
    }

    MembershipDto membership = userService.findMembershipById(membershipId)
            .orElseThrow(() -> new BusinessException(
                    MEMBERSHIP_NOT_FOUND + ": " + membershipId,
                    OffboardingErrorCode.RESOURCE_NOT_FOUND));

    UUID companyId = membership.getCompanyId();

    OffboardingProcess process = OffboardingProcess.create(
            membershipId, companyId, templateId, lastWorkingDay, reason, initiatedBy);
    OffboardingProcess savedProcess = processRepository.save(process);

    List<OffboardingTaskTemplate> taskTemplates = taskTemplateRepository.findByTemplateId(templateId);
    if (taskTemplates.isEmpty()) {
      log.warn("No tasks defined for offboarding template: {}", templateId);
    }

    for (OffboardingTaskTemplate tt : taskTemplates) {
      UUID assigneeId = resolveAssigneeByRole(companyId, membershipId, tt.getAssigneeRole());

      OffboardingTask task = OffboardingTask.create(
              savedProcess.getId(),
              tt.getTitle(),
              tt.getDescription(),
              assigneeId,
              LocalDate.now().plusDays(tt.getDaysFromStart())
      );
      OffboardingTask savedTask = taskRepository.save(task);

      eventPublisher.publishEvent(new OffboardingTaskAssignedEvent(
              this, savedTask.getId(), assigneeId, savedTask.getTitle()));

      log.debug("Created offboarding task: {} for assignee: {}", savedTask.getTitle(), assigneeId);
    }

    OffboardingProcess activeProcess = OffboardingProcess.builder()
            .id(savedProcess.getId())
            .membershipId(savedProcess.getMembershipId())
            .companyId(savedProcess.getCompanyId())
            .templateId(savedProcess.getTemplateId())
            .status(OffboardingProcessStatus.PENDING_APPROVAL)
            .startDate(savedProcess.getStartDate())
            .lastWorkingDay(savedProcess.getLastWorkingDay())
            .reason(savedProcess.getReason())
            .initiatedBy(savedProcess.getInitiatedBy())
            .createdAt(savedProcess.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();
    processRepository.save(activeProcess);

    log.info("Offboarding process started with id: {}", savedProcess.getId());
    return activeProcess;
  }

  public OffboardingProcess approveProcess(UUID processId) {
    OffboardingProcess process = processRepository.findById(processId)
            .orElseThrow(() -> new BusinessException(
                    PROCESS_NOT_FOUND, OffboardingErrorCode.RESOURCE_NOT_FOUND));

    if (process.getStatus() != OffboardingProcessStatus.PENDING_APPROVAL) {
      throw new BusinessException(
              "Process must be in PENDING_APPROVAL status. Current: " + process.getStatus(),
              OffboardingErrorCode.INVALID_STATUS_TRANSITION);
    }

    OffboardingProcess updatedProcess = OffboardingProcess.builder()
            .id(process.getId())
            .membershipId(process.getMembershipId())
            .companyId(process.getCompanyId())
            .templateId(process.getTemplateId())
            .status(OffboardingProcessStatus.HR_INTERVIEW)
            .startDate(process.getStartDate())
            .lastWorkingDay(process.getLastWorkingDay())
            .reason(process.getReason())
            .initiatedBy(process.getInitiatedBy())
            .createdAt(process.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

    processRepository.save(updatedProcess);
    log.info("Offboarding process approved: {}", processId);
    return updatedProcess;
  }

  public OffboardingProcess transitionProcess(UUID processId, OffboardingProcessStatus newStatus) {
    OffboardingProcess process = processRepository.findById(processId)
            .orElseThrow(() -> new BusinessException(
                    PROCESS_NOT_FOUND, OffboardingErrorCode.RESOURCE_NOT_FOUND));

    if (process.getStatus() == OffboardingProcessStatus.COMPLETED) {
      throw new BusinessException(
              "Cannot transition completed process",
              OffboardingErrorCode.CANT_TRANSITION_COMPLETED);
    }

    if (process.getStatus() == OffboardingProcessStatus.CANCELLED) {
      throw new BusinessException(
              "Cannot transition cancelled process",
              OffboardingErrorCode.CANT_TRANSITION_CANCELLED);
    }

    OffboardingProcess updatedProcess = OffboardingProcess.builder()
            .id(process.getId())
            .membershipId(process.getMembershipId())
            .companyId(process.getCompanyId())
            .templateId(process.getTemplateId())
            .status(newStatus)
            .startDate(process.getStartDate())
            .lastWorkingDay(process.getLastWorkingDay())
            .reason(process.getReason())
            .initiatedBy(process.getInitiatedBy())
            .createdAt(process.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

    processRepository.save(updatedProcess);
    log.info("Offboarding process transitioned: {} -> {}", processId, newStatus);
    return updatedProcess;
  }

  public OffboardingProcess getProcess(UUID processId) {
    return processRepository.findById(processId)
            .orElseThrow(() -> new BusinessException(
                    PROCESS_NOT_FOUND, OffboardingErrorCode.RESOURCE_NOT_FOUND));
  }

  public OffboardingProcess getProcessByMembershipId(UUID membershipId) {
    return processRepository.findByMembershipId(membershipId)
            .orElseThrow(() -> new BusinessException(
                    "Process not found for membership: " + membershipId,
                    OffboardingErrorCode.RESOURCE_NOT_FOUND));
  }

  public List<OffboardingProcess> getProcessesByCompany(UUID companyId) {
    return processRepository.findByCompanyId(companyId);
  }

  public List<OffboardingTask> getProcessTasks(UUID processId) {
    return taskRepository.findByProcessId(processId);
  }

  public OffboardingTask getTask(UUID taskId) {
    return taskRepository.findById(taskId)
            .orElseThrow(() -> new BusinessException(
                    TASK_NOT_FOUND, OffboardingErrorCode.RESOURCE_NOT_FOUND));
  }

  public void startTask(UUID taskId) {
    OffboardingTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new BusinessException(
                    TASK_NOT_FOUND, OffboardingErrorCode.RESOURCE_NOT_FOUND));

    if (task.getStatus() != OffboardingTaskStatus.PENDING) {
      throw new BusinessException(
              "Task cannot be started. Current status: " + task.getStatus(),
              OffboardingErrorCode.TASK_NOT_PENDING);
    }

    OffboardingTask updatedTask = OffboardingTask.builder()
            .id(task.getId())
            .processId(task.getProcessId())
            .title(task.getTitle())
            .description(task.getDescription())
            .assigneeId(task.getAssigneeId())
            .status(OffboardingTaskStatus.IN_PROGRESS)
            .dueDate(task.getDueDate())
            .completedAt(null)
            .createdAt(task.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

    taskRepository.save(updatedTask);
    log.debug("Task started: {}", taskId);
  }

  public void completeTask(UUID taskId) {
    OffboardingTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new BusinessException(
                    TASK_NOT_FOUND, OffboardingErrorCode.RESOURCE_NOT_FOUND));

    if (task.getStatus() == OffboardingTaskStatus.DONE) {
      throw new BusinessException(
              "Task already completed", OffboardingErrorCode.TASK_ALREADY_COMPLETED);
    }

    if (task.getStatus() == OffboardingTaskStatus.SKIPPED) {
      throw new BusinessException(
              "Task already skipped", OffboardingErrorCode.TASK_ALREADY_SKIPPED);
    }

    if (task.getStatus() == OffboardingTaskStatus.BLOCKED) {
      throw new BusinessException(
              "Task is blocked, cannot complete", OffboardingErrorCode.TASK_BLOCKED);
    }

    OffboardingTask updatedTask = OffboardingTask.builder()
            .id(task.getId())
            .processId(task.getProcessId())
            .title(task.getTitle())
            .description(task.getDescription())
            .assigneeId(task.getAssigneeId())
            .status(OffboardingTaskStatus.DONE)
            .dueDate(task.getDueDate())
            .completedAt(LocalDateTime.now())
            .createdAt(task.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

    taskRepository.save(updatedTask);
    log.info("Task completed: {}", taskId);

    checkAndCompleteProcess(task.getProcessId());
  }

  public void skipTask(UUID taskId) {
    OffboardingTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new BusinessException(
                    TASK_NOT_FOUND, OffboardingErrorCode.RESOURCE_NOT_FOUND));

    if (task.getStatus() == OffboardingTaskStatus.DONE) {
      throw new BusinessException(
              "Task already completed", OffboardingErrorCode.TASK_ALREADY_COMPLETED);
    }

    OffboardingTask updatedTask = OffboardingTask.builder()
            .id(task.getId())
            .processId(task.getProcessId())
            .title(task.getTitle())
            .description(task.getDescription())
            .assigneeId(task.getAssigneeId())
            .status(OffboardingTaskStatus.SKIPPED)
            .dueDate(task.getDueDate())
            .completedAt(LocalDateTime.now())
            .createdAt(task.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

    taskRepository.save(updatedTask);
    log.info("Task skipped: {}", taskId);

    checkAndCompleteProcess(task.getProcessId());
  }

  public void blockTask(UUID taskId) {
    OffboardingTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new BusinessException(
                    TASK_NOT_FOUND, OffboardingErrorCode.RESOURCE_NOT_FOUND));

    if (task.getStatus() == OffboardingTaskStatus.DONE) {
      throw new BusinessException(
              "Cannot block completed task", OffboardingErrorCode.TASK_ALREADY_COMPLETED);
    }

    if (task.getStatus() == OffboardingTaskStatus.SKIPPED) {
      throw new BusinessException(
              "Cannot block skipped task", OffboardingErrorCode.TASK_ALREADY_SKIPPED);
    }

    OffboardingTask updatedTask = OffboardingTask.builder()
            .id(task.getId())
            .processId(task.getProcessId())
            .title(task.getTitle())
            .description(task.getDescription())
            .assigneeId(task.getAssigneeId())
            .status(OffboardingTaskStatus.BLOCKED)
            .dueDate(task.getDueDate())
            .completedAt(task.getCompletedAt())
            .createdAt(task.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

    taskRepository.save(updatedTask);
    log.info("Task blocked: {}", taskId);
  }

  public void unblockTask(UUID taskId) {
    OffboardingTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new BusinessException(
                    TASK_NOT_FOUND, OffboardingErrorCode.RESOURCE_NOT_FOUND));

    if (task.getStatus() != OffboardingTaskStatus.BLOCKED) {
      throw new BusinessException(
              "Task is not blocked. Current status: " + task.getStatus(),
              OffboardingErrorCode.TASK_NOT_BLOCKED);
    }

    OffboardingTask updatedTask = OffboardingTask.builder()
            .id(task.getId())
            .processId(task.getProcessId())
            .title(task.getTitle())
            .description(task.getDescription())
            .assigneeId(task.getAssigneeId())
            .status(OffboardingTaskStatus.PENDING)
            .dueDate(task.getDueDate())
            .completedAt(task.getCompletedAt())
            .createdAt(task.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

    taskRepository.save(updatedTask);
    log.info("Task unblocked: {}", taskId);
  }

  public List<OffboardingTask> getTasksByAssignee(UUID assigneeId) {
    return taskRepository.findByAssigneeId(assigneeId);
  }

  public void cancelOffboarding(UUID processId) {
    OffboardingProcess process = processRepository.findById(processId)
            .orElseThrow(() -> new BusinessException(
                    PROCESS_NOT_FOUND, OffboardingErrorCode.RESOURCE_NOT_FOUND));

    if (process.getStatus() == OffboardingProcessStatus.COMPLETED) {
      throw new BusinessException(
              "Cannot cancel completed offboarding",
              OffboardingErrorCode.CANT_CANCEL_COMPLETED);
    }

    if (process.getStatus() == OffboardingProcessStatus.CANCELLED) {
      throw new BusinessException(
              "Process already cancelled",
              OffboardingErrorCode.PROCESS_ALREADY_CANCELLED);
    }

    OffboardingProcess updatedProcess = OffboardingProcess.builder()
            .id(process.getId())
            .membershipId(process.getMembershipId())
            .companyId(process.getCompanyId())
            .templateId(process.getTemplateId())
            .status(OffboardingProcessStatus.CANCELLED)
            .startDate(process.getStartDate())
            .lastWorkingDay(process.getLastWorkingDay())
            .reason(process.getReason())
            .initiatedBy(process.getInitiatedBy())
            .createdAt(process.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

    processRepository.save(updatedProcess);
    log.info("Offboarding process cancelled: {}", processId);
  }

  public OffboardingProcess signOffProcess(UUID processId) {
    OffboardingProcess process = processRepository.findById(processId)
            .orElseThrow(() -> new BusinessException(
                    PROCESS_NOT_FOUND, OffboardingErrorCode.RESOURCE_NOT_FOUND));

    if (process.getStatus() != OffboardingProcessStatus.READY_FOR_SIGN_OFF) {
      throw new BusinessException(
              "Process must be in READY_FOR_SIGN_OFF status. Current: " + process.getStatus(),
              OffboardingErrorCode.INVALID_STATUS_TRANSITION);
    }

    OffboardingProcess completedProcess = OffboardingProcess.builder()
            .id(process.getId())
            .membershipId(process.getMembershipId())
            .companyId(process.getCompanyId())
            .templateId(process.getTemplateId())
            .status(OffboardingProcessStatus.COMPLETED)
            .startDate(process.getStartDate())
            .lastWorkingDay(process.getLastWorkingDay())
            .reason(process.getReason())
            .initiatedBy(process.getInitiatedBy())
            .createdAt(process.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

    processRepository.save(completedProcess);
    log.info("Offboarding process signed off and completed: {}", processId);

    MembershipDto membership = userService.findMembershipById(process.getMembershipId())
            .orElse(null);

    if (membership != null) {
      EmployeeOffboardedEvent event = new EmployeeOffboardedEvent(
              this,
              process.getMembershipId(),
              membership.getUserId(),
              membership.getCompanyId()
      );
      eventPublisher.publishEvent(event);
      log.info("Published EmployeeOffboardedEvent for membership: {}", process.getMembershipId());
    } else {
      log.warn("Membership not found for completed offboarding process: {}", processId);
    }

    return completedProcess;
  }

  private void checkAndCompleteProcess(UUID processId) {
    List<OffboardingTask> tasks = taskRepository.findByProcessId(processId);
    boolean allDoneOrSkipped = tasks.stream()
            .allMatch(t -> t.getStatus() == OffboardingTaskStatus.DONE ||
                    t.getStatus() == OffboardingTaskStatus.SKIPPED);

    if (!allDoneOrSkipped) {
      return;
    }

    OffboardingProcess process = processRepository.findById(processId)
            .orElseThrow(() -> new BusinessException(
                    PROCESS_NOT_FOUND, OffboardingErrorCode.RESOURCE_NOT_FOUND));

    OffboardingProcess completedProcess = OffboardingProcess.builder()
            .id(process.getId())
            .membershipId(process.getMembershipId())
            .companyId(process.getCompanyId())
            .templateId(process.getTemplateId())
            .status(OffboardingProcessStatus.READY_FOR_SIGN_OFF)
            .startDate(process.getStartDate())
            .lastWorkingDay(process.getLastWorkingDay())
            .reason(process.getReason())
            .initiatedBy(process.getInitiatedBy())
            .createdAt(process.getCreatedAt())
            .updatedAt(LocalDateTime.now())
            .build();

    processRepository.save(completedProcess);
    log.info("Offboarding process ready for sign-off: {}", processId);
  }

  private UUID resolveAssigneeByRole(UUID companyId, UUID membershipId, String role) {
    log.debug("Resolving assignee for role: {}, companyId: {}, membershipId: {}", role, companyId, membershipId);

    return switch (role.toUpperCase()) {
      case "EMPLOYEE" -> {
        MembershipDto membership = userService.findMembershipById(membershipId)
                .orElseThrow(() -> new BusinessException(
                        MEMBERSHIP_NOT_FOUND, OffboardingErrorCode.RESOURCE_NOT_FOUND));
        yield membership.getUserId();
      }
      case "MANAGER" -> findUserByRoleOrThrow(companyId, "MANAGER", "manager");
      case "HR" -> findUserByRoleOrThrow(companyId, "HR", "HR");
      case "IT_ADMIN", "IT" -> findUserByRoleOrThrow(companyId, "IT_ADMIN", "IT Admin");
      case "ACCOUNTING", "FINANCE" -> findUserByRoleOrThrow(companyId, "ACCOUNTING", "Accounting");
      default -> throw new BusinessException(
              "Unknown role: " + role,
              OffboardingErrorCode.UNKNOWN_ROLE);
    };
  }

  private UUID findUserByRoleOrThrow(UUID companyId, String roleName, String roleDisplayName) {
    return getAllMembershipsByCompany(companyId).stream()
            .filter(m -> roleName.equals(m.getRole()))
            .findFirst()
            .map(MembershipDto::getUserId)
            .orElseThrow(() -> new BusinessException(
                    "No " + roleDisplayName + " found for company: " + companyId,
                    OffboardingErrorCode.NO_EMPLOYEE_FOR_ROLE));
  }

  private List<MembershipDto> getAllMembershipsByCompany(UUID companyId) {
    return userService.getUsersByCompanyId(companyId).stream()
            .map(user -> userService.findFirstActiveMembershipByUserId(user.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
  }
}