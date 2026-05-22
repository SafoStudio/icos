package com.safo.icos.modules.onboarding.presentation;

import com.safo.icos.modules.onboarding.application.OnboardingService;
import com.safo.icos.modules.onboarding.domain.model.*;
import com.safo.icos.modules.onboarding.domain.repository.OnboardingProcessRepository;
import com.safo.icos.modules.onboarding.domain.repository.OnboardingTemplateRepository;
import com.safo.icos.modules.onboarding.domain.repository.OnboardingTaskRepository;
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
public class OnboardingGraphQLResolver {
    private final OnboardingService onboardingService;
    private final OnboardingProcessRepository processRepository;
    private final OnboardingTemplateRepository templateRepository;
    private final OnboardingTaskRepository taskRepository;

    @QueryMapping
    @RequirePermission("onboarding:read")
    public OnboardingProcess onboardingProcess(@Argument UUID id) {
        return processRepository.findById(id).orElse(null);
    }

    @QueryMapping
    @RequirePermission("onboarding:task.complete")
    public List<OnboardingTask> myOnboardingTasks() {
        IcosUserPrincipal principal = (IcosUserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return taskRepository.findByAssigneeId(principal.getUserId());
    }

    @QueryMapping
    @RequirePermission("onboarding:read")
    public List<OnboardingTemplate> onboardingTemplates(@Argument UUID companyId) {
        return templateRepository.findByCompanyId(companyId);
    }

    @MutationMapping
    @RequirePermission("onboarding:manage")
    public OnboardingTemplate createOnboardingTemplate(@Argument CreateOnboardingTemplateInput input) {
        return onboardingService.createTemplate(input.companyId(), input.name(), input.description());
    }

    @MutationMapping
    @RequirePermission("onboarding:manage")
    public OnboardingProcess startOnboarding(@Argument StartOnboardingInput input) {
        return onboardingService.startOnboarding(input.membershipId(), input.templateId(), LocalDate.parse(input.startDate()));
    }

    @MutationMapping
    @RequirePermission("onboarding:task.complete")
    public OnboardingTask completeOnboardingTask(@Argument UUID taskId) {
        onboardingService.completeTask(taskId);
        return taskRepository.findById(taskId).orElseThrow();
    }

    @SchemaMapping(typeName = "OnboardingProcess")
    public List<OnboardingTask> tasks(OnboardingProcess process) {
        return onboardingService.getProcessTasks(process.getId());
    }

    public record CreateOnboardingTemplateInput(UUID companyId, String name, String description) {}
    public record StartOnboardingInput(UUID membershipId, UUID templateId, String startDate) {}
}
