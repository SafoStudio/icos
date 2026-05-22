package com.safo.icos.modules.onboarding.application;

import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.common.constant.ErrorCode;
import com.safo.icos.modules.onboarding.domain.model.*;
import com.safo.icos.modules.onboarding.domain.repository.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
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

    public OnboardingTemplate createTemplate(UUID companyId, String name, String description) {
        OnboardingTemplate template = OnboardingTemplate.create(companyId, name, description);
        return templateRepository.save(template);
    }

    public void addTaskToTemplate(UUID templateId, String title, String description, Integer daysFromStart) {
        OnboardingTaskTemplate taskTemplate = OnboardingTaskTemplate.create(templateId, title, description, daysFromStart);
        taskTemplateRepository.save(taskTemplate);
    }

    public OnboardingProcess startOnboarding(UUID membershipId, UUID templateId, LocalDate startDate) {
        OnboardingProcess process = OnboardingProcess.create(membershipId, templateId, startDate);
        OnboardingProcess savedProcess = processRepository.save(process);

        // Instantiate tasks from template
        List<OnboardingTaskTemplate> taskTemplates = taskTemplateRepository.findByTemplateId(templateId);
        for (OnboardingTaskTemplate tt : taskTemplates) {
            OnboardingTask task = OnboardingTask.create(
                    savedProcess.getId(),
                    tt.getTitle(),
                    tt.getDescription(),
                    UUID.randomUUID(), // TODO: Actual assignee logic (e.g., HR or IT user)
                    startDate.plusDays(tt.getDaysFromStart())
            );
            taskRepository.save(task);
        }

        return savedProcess;
    }

    public List<OnboardingTask> getProcessTasks(UUID processId) {
        return taskRepository.findByProcessId(processId);
    }

    public void completeTask(UUID taskId) {
        OnboardingTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException("Task not found", ErrorCode.NOT_FOUND));

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
