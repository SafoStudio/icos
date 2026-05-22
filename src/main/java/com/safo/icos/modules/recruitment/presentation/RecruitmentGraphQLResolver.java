package com.safo.icos.modules.recruitment.presentation;

import com.safo.icos.modules.recruitment.application.ApplicationService;
import com.safo.icos.modules.recruitment.application.CandidateService;
import com.safo.icos.modules.recruitment.application.VacancyService;
import com.safo.icos.modules.recruitment.application.dto.ApplicationDto;
import com.safo.icos.modules.recruitment.application.dto.CreateVacancyRequest;
import com.safo.icos.modules.recruitment.application.dto.VacancyDto;
import com.safo.icos.modules.recruitment.domain.model.Candidate;
import com.safo.icos.security.permission.RequirePermission;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class RecruitmentGraphQLResolver {
    private final VacancyService vacancyService;
    private final ApplicationService applicationService;
    private final CandidateService candidateService;

    @QueryMapping
    @RequirePermission("recruitment:read")
    public List<VacancyDto> vacancies(@Argument UUID companyId) {
        return vacancyService.getVacanciesByCompany(companyId);
    }

    @QueryMapping
    @RequirePermission("recruitment:read")
    public VacancyDto vacancy(@Argument UUID id) {
        return vacancyService.getVacancy(id);
    }

    @QueryMapping
    @RequirePermission("application:manage")
    public ApplicationDto application(@Argument UUID id) {
        return applicationService.getApplication(id);
    }

    @MutationMapping
    @RequirePermission("recruitment:manage")
    public VacancyDto createVacancy(@Argument CreateVacancyRequest input) {
        return vacancyService.createVacancy(input);
    }

    @MutationMapping
    @RequirePermission("application:manage")
    public ApplicationDto applyToVacancy(@Argument UUID vacancyId, @Argument CandidateInput candidate) {
        Candidate savedCandidate = candidateService.createCandidate(
                candidate.firstName(),
                candidate.lastName(),
                candidate.email(),
                candidate.phone(),
                candidate.source(),
                candidate.resumeUrl()
        );
        return applicationService.createApplication(savedCandidate.getId(), vacancyId);
    }

    @MutationMapping
    @RequirePermission("application:manage")
    public ApplicationDto moveApplication(@Argument UUID applicationId, @Argument UUID stageId) {
        applicationService.moveToStage(applicationId, stageId);
        return applicationService.getApplication(applicationId);
    }

    // Input DTO for GraphQL
    public record CandidateInput(
            String firstName,
            String lastName,
            String email,
            String phone,
            String source,
            String resumeUrl
    ) {}
}
