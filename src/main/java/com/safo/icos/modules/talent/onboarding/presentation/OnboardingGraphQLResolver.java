package com.safo.icos.modules.talent.onboarding.presentation;

import com.safo.icos.modules.auth.api.constant.AuthErrorCode;
import com.safo.icos.common.exception.BusinessException;
import com.safo.icos.modules.talent.onboarding.application.OnboardingService;
import com.safo.icos.modules.talent.onboarding.api.dto.OnboardingProcessDto;
import com.safo.icos.modules.talent.onboarding.api.dto.OnboardingTaskDto;
import com.safo.icos.modules.talent.onboarding.api.dto.OnboardingTemplateDto;
import com.safo.icos.modules.talent.onboarding.infrastructure.mapper.OnboardingMapper;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingProcessRepository;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingTaskRepository;
import com.safo.icos.modules.talent.onboarding.domain.repository.OnboardingTemplateRepository;
import com.safo.icos.security.context.IcosUserPrincipal;
import com.safo.icos.security.permission.RequirePermission;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class OnboardingGraphQLResolver {
    private final OnboardingService onboardingService;
    private final OnboardingProcessRepository processRepository;
    private final OnboardingTemplateRepository templateRepository;
    private final OnboardingTaskRepository taskRepository;
    private final OnboardingMapper mapper;

    @QueryMapping
    @RequirePermission("onboarding:read")
    public OnboardingProcessDto onboardingProcess(@Argument UUID id) {
        return mapper.toDto(processRepository.findById(id).orElse(null));
    }

    @QueryMapping
    @RequirePermission("onboarding:task.complete")
    public List<OnboardingTaskDto> myOnboardingTasks() {
        IcosUserPrincipal principal = getPrincipal();
        return taskRepository.findByAssigneeId(principal.getUserId()).stream().map(mapper::toDto).toList();
    }

    @QueryMapping
    @RequirePermission("onboarding:read")
    public List<OnboardingTemplateDto> onboardingTemplates(@Argument UUID companyId) {
        return templateRepository.findByCompanyId(companyId).stream().map(mapper::toDto).toList();
    }

    @MutationMapping
    @RequirePermission("onboarding:manage")
    public OnboardingTemplateDto createOnboardingTemplate(@Argument CreateOnboardingTemplateInput input) {
        return mapper.toDto(onboardingService.createTemplate(input.companyId(), input.name(), input.description()));
    }

    @MutationMapping
    @RequirePermission("onboarding:manage")
    public OnboardingProcessDto startOnboarding(@Argument StartOnboardingInput input) {
        return mapper.toDto(onboardingService.startOnboarding(input.membershipId(), input.templateId(), LocalDate.parse(input.startDate())));
    }

    @MutationMapping
    @RequirePermission("onboarding:task.complete")
    public OnboardingTaskDto completeOnboardingTask(@Argument UUID taskId) {
        onboardingService.completeTask(taskId);
        return mapper.toDto(taskRepository.findById(taskId).orElseThrow());
    }

    @SchemaMapping(typeName = "OnboardingProcess")
    public List<OnboardingTaskDto> tasks(OnboardingProcessDto process) {
        return onboardingService.getProcessTasks(process.getId()).stream().map(mapper::toDto).toList();
    }

    public record CreateOnboardingTemplateInput(UUID companyId, String name, String description) {}
    public record StartOnboardingInput(UUID membershipId, UUID templateId, String startDate) {}

    private IcosUserPrincipal getPrincipal() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof IcosUserPrincipal principal)) {
            throw new BusinessException("User not authenticated", AuthErrorCode.TOKEN_NOT_FOUND);
        }
        return principal;
    }
}
