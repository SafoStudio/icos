package com.safo.icos.modules.talent.onboarding.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.identity.user.api.UserProvider;
import com.safo.icos.modules.identity.user.api.dto.MembershipDto;
import com.safo.icos.modules.talent.onboarding.api.constant.OnboardingErrorCode;
import com.safo.icos.modules.talent.onboarding.api.event.OnboardingTaskAssignedEvent;
import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingProcess;
import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTask;
import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTaskTemplate;
import com.safo.icos.modules.talent.onboarding.domain.model.OnboardingTemplate;
import com.safo.icos.modules.talent.onboarding.domain.model.enums.OnboardingProcessStatus;
import com.safo.icos.modules.talent.onboarding.domain.model.enums.OnboardingTaskStatus;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingProcessRepository;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingTaskRepository;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingTaskTemplateRepository;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingTemplateRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service for employee onboarding.
 * Creates onboarding processes from templates, assigns tasks,
 * and tracks completion.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class OnboardingService {

  private final OnboardingProcessRepository processRepository;
  private final OnboardingTaskRepository taskRepository;
  private final OnboardingTemplateRepository templateRepository;
  private final OnboardingTaskTemplateRepository taskTemplateRepository;
  private final UserProvider userProvider;
  private final ApplicationEventPublisher eventPublisher;

  /**
   * Creates a new onboarding template for a company.
   *
   * @param companyId   the owning company
   * @param name        template name
   * @param description template description
   * @return the created template
   */
  public OnboardingTemplate createTemplate(UUID companyId, String name, String description) {
    OnboardingTemplate template = OnboardingTemplate.create(companyId, name, description);
    return templateRepository.save(template);
  }

  /**
   * Starts an onboarding process for a new member using the given template.
   * Instantiates all template tasks and publishes assignment events.
   *
   * @param membershipId the new member's membership ID
   * @param templateId   the template to use
   * @param startDate    the first day of onboarding
   * @return the created onboarding process
   * @throws BusinessException if the membership is not found
   */
  public OnboardingProcess startOnboarding(UUID membershipId, UUID templateId, LocalDate startDate) {
    OnboardingProcess process = OnboardingProcess.create(membershipId, templateId, startDate);
    OnboardingProcess savedProcess = processRepository.save(process);

    UUID userId = userProvider.findMembershipById(membershipId)
            .map(MembershipDto::getUserId)
            .orElseThrow(() -> new BusinessException(
                    "Membership not found", OnboardingErrorCode.RESOURCE_NOT_FOUND));

    List<OnboardingTaskTemplate> taskTemplates =
            taskTemplateRepository.findByTemplateId(templateId);
    for (OnboardingTaskTemplate tt : taskTemplates) {
      OnboardingTask task = OnboardingTask.create(
              savedProcess.getId(),
              tt.getTitle(),
              tt.getDescription(),
              userId,
              startDate.plusDays(tt.getDaysFromStart())
      );
      OnboardingTask savedTask = taskRepository.save(task);

      eventPublisher.publishEvent(new OnboardingTaskAssignedEvent(
              this, savedTask.getId(), userId, savedTask.getTitle()));
    }

    return savedProcess;
  }

  /**
   * Returns all tasks for a given onboarding process.
   *
   * @param processId the process UUID
   * @return list of tasks
   */
  public List<OnboardingTask> getProcessTasks(UUID processId) {
    return taskRepository.findByProcessId(processId);
  }

  /**
   * Marks a task as completed.
   * If all tasks in the process are done, the process is marked as completed.
   *
   * @param taskId the task UUID
   * @throws BusinessException if the task is not found
   */
  public void completeTask(UUID taskId) {
    OnboardingTask task = taskRepository.findById(taskId)
            .orElseThrow(() -> new BusinessException(
                    "Task not found", OnboardingErrorCode.RESOURCE_NOT_FOUND));

    OnboardingTask updatedTask = OnboardingTask.builder()
            .id(task.getId())
            .processId(task.getProcessId())
            .title(task.getTitle())
            .description(task.getDescription())
            .assigneeId(task.getAssigneeId())
            .status(OnboardingTaskStatus.DONE)
            .dueDate(task.getDueDate())
            .completedAt(java.time.LocalDateTime.now())
            .createdAt(task.getCreatedAt())
            .updatedAt(java.time.LocalDateTime.now())
            .build();

    taskRepository.save(updatedTask);
    checkAndCompleteProcess(task.getProcessId());
  }

  private void checkAndCompleteProcess(UUID processId) {
    List<OnboardingTask> tasks = taskRepository.findByProcessId(processId);
    boolean allDone = tasks.stream()
            .allMatch(t -> t.getStatus() == OnboardingTaskStatus.DONE);

    if (allDone) {
      OnboardingProcess process = processRepository.findById(processId).orElseThrow();
      OnboardingProcess updatedProcess = OnboardingProcess.builder()
              .id(process.getId())
              .membershipId(process.getMembershipId())
              .templateId(process.getTemplateId())
              .status(OnboardingProcessStatus.COMPLETED)
              .startDate(process.getStartDate())
              .targetCompletionDate(process.getTargetCompletionDate())
              .createdAt(process.getCreatedAt())
              .updatedAt(java.time.LocalDateTime.now())
              .build();
      processRepository.save(updatedProcess);
    }
  }
}