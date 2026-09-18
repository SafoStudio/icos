package com.safo.icos.modules.performance.presentation;

import com.safo.icos.modules.performance.api.dto.ObjectiveDto;
import com.safo.icos.modules.performance.api.dto.PerformanceReviewDto;
import com.safo.icos.modules.performance.application.OkrService;
import com.safo.icos.modules.performance.application.PerformanceService;
import com.safo.icos.modules.performance.infrastructure.mapper.PerformanceMapper;
import com.safo.icos.security.context.IcosUserPrincipal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PerformanceGraphQLResolver {

    private final OkrService okrService;
    private final PerformanceService performanceService;
    private final PerformanceMapper mapper;

    @QueryMapping
    public List<ObjectiveDto> myObjectives(@AuthenticationPrincipal IcosUserPrincipal principal) {
        if (principal == null) return List.of();
        try {
            return okrService.getUserObjectives(principal.getUserId()).stream().map(mapper::toDto).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @QueryMapping
    public List<ObjectiveDto> userObjectives(@Argument UUID userId) {
        try {
            return okrService.getUserObjectives(userId).stream().map(mapper::toDto).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @QueryMapping
    public List<PerformanceReviewDto> employeeReviews(@Argument UUID employeeId) {
        try {
            return performanceService.getEmployeeReviews(employeeId).stream().map(mapper::toDto).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @MutationMapping
    public ObjectiveDto createObjective(@AuthenticationPrincipal IcosUserPrincipal principal,
                                     @Argument String title, @Argument String description, @Argument String startDate, @Argument String endDate) {
        UUID userId = principal != null ? principal.getUserId() : UUID.randomUUID();
        return mapper.toDto(okrService.createObjective(userId, title, description, LocalDate.parse(startDate), LocalDate.parse(endDate)));
    }

    @MutationMapping
    public ObjectiveDto createUserObjective(@Argument UUID userId,
                                         @Argument String title, @Argument String description, @Argument String startDate, @Argument String endDate) {
        return mapper.toDto(okrService.createObjective(userId, title, description, LocalDate.parse(startDate), LocalDate.parse(endDate)));
    }

    @MutationMapping
    public PerformanceReviewDto finalizeReview(@Argument UUID id, @Argument Integer rating, @Argument String summary) {
        return mapper.toDto(performanceService.finalizeReview(id, rating, summary));
    }

    @MutationMapping
    public PerformanceReviewDto submitReview(@AuthenticationPrincipal IcosUserPrincipal principal,
                                            @Argument UUID employeeId, @Argument Integer rating, @Argument String summary) {
        UUID managerId = principal != null ? principal.getUserId() : employeeId;
        return mapper.toDto(performanceService.submitReview(employeeId, managerId, rating, summary));
    }
}
