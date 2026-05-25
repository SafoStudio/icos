package com.safo.icos.modules.performance.presentation;

import com.safo.icos.modules.performance.application.OkrService;
import com.safo.icos.modules.performance.application.PerformanceService;
import com.safo.icos.modules.performance.domain.model.Objective;
import com.safo.icos.modules.performance.domain.model.PerformanceReview;
import com.safo.icos.security.context.IcosUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PerformanceGraphQLResolver {

    private final OkrService okrService;
    private final PerformanceService performanceService;

    @QueryMapping
    public List<Objective> myObjectives(@AuthenticationPrincipal IcosUserPrincipal principal) {
        return okrService.getUserObjectives(principal.getUserId());
    }

    @QueryMapping
    public List<PerformanceReview> employeeReviews(@Argument UUID employeeId) {
        return performanceService.getEmployeeReviews(employeeId);
    }

    @MutationMapping
    public Objective createObjective(@AuthenticationPrincipal IcosUserPrincipal principal, 
                                     @Argument String title, @Argument String description, @Argument String startDate, @Argument String endDate) {
        return okrService.createObjective(principal.getUserId(), title, description, LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @MutationMapping
    public PerformanceReview finalizeReview(@Argument UUID id, @Argument Integer rating, @Argument String summary) {
        return performanceService.finalizeReview(id, rating, summary);
    }
}
