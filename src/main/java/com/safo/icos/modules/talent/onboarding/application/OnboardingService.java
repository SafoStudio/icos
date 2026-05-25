package com.safo.icos.modules.talent.onboarding.application;

import com.safo.icos.modules.user.api.dto.MembershipDto;
import com.safo.icos.modules.user.api.UserProvider;
import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.talent.onboarding.api.constant.OnboardingErrorCode;
import com.safo.icos.modules.talent.onboarding.api.event.OnboardingTaskAssignedEvent;
import com.safo.icos.modules.talent.onboarding.domain.model.*;
import com.safo.icos.modules.talent.onboarding.domain.model.enums.*;
import com.safo.icos.modules.talent.onboarding.domain.repository.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public OnboardingTemplate createTemplate(UUID companyId, String name, String description) {
        OnboardingTemplate template = OnboardingTemplate.create(companyId, name, description);
        return templateRepository.save(template);
    }

    public OnboardingProcess startOnboarding(UUID membershipId, UUID templateId, LocalDate startDate) {
        OnboardingProcess process = OnboardingProcess.create(membershipId, templateId, startDate);
        OnboardingProcess savedProcess = processRepository.save(process);

        // Get the user ID associated with this membership
        UUID userId = userProvider.findMembershipById(membershipId)
                .map(MembershipDto::getUserId)
                .orElseThrow(() -> new BusinessException("Membership not found", OnboardingErrorCode.RESOURCE_NOT_FOUND));

        // Instantiate tasks from template
        List<OnboardingTaskTemplate> taskTemplates = taskTemplateRepository.findByTemplateId(templateId);
        for (OnboardingTaskTemplate tt : taskTemplates) {
            OnboardingTask task = OnboardingTask.create(
                    savedProcess.getId(),
                    tt.getTitle(),
                    tt.getDescription(),
                    userId, // Assign to the user being onboarded for self-service tasks
                    startDate.plusDays(tt.getDaysFromStart())
            );
            OnboardingTask savedTask = taskRepository.save(task);

            // Publish event for notifications
            eventPublisher.publishEvent(new OnboardingTaskAssignedEvent(
                    this,
                    savedTask.getId(),
                    userId,
                    savedTask.getTitle()
            ));
        }

        return savedProcess;
    }

    public List<OnboardingTask> getProcessTasks(UUID processId) {
        return taskRepository.findByProcessId(processId);
    }

    public void completeTask(UUID taskId) {
        OnboardingTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("Task not found", OnboardingErrorCode.RESOURCE_NOT_FOUND));

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
        
        // Check if all tasks are done to complete the process
        checkAndCompleteProcess(task.getProcessId());
    }

    private void checkAndCompleteProcess(UUID processId) {
        List<OnboardingTask> tasks = taskRepository.findByProcessId(processId);
        boolean allDone = tasks.stream().allMatch(t -> t.getStatus() == OnboardingTaskStatus.DONE);

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
