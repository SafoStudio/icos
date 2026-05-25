package com.safo.icos.modules.compensation.presentation;

import com.safo.icos.modules.compensation.application.CompensationService;
import com.safo.icos.modules.compensation.domain.model.CompensationRecord;
import com.safo.icos.modules.compensation.domain.model.SalaryRevision;
import com.safo.icos.security.context.IcosUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class CompensationGraphQLResolver {

    private final CompensationService compensationService;

    @QueryMapping
    public CompensationRecord compensation(@Argument UUID userId) {
        return compensationService.getCompensation(userId);
    }

    @QueryMapping
    public List<SalaryRevision> salaryHistory(@Argument UUID userId) {
        return compensationService.getSalaryHistory(userId);
    }

    @MutationMapping
    public CompensationRecord updateSalary(@AuthenticationPrincipal IcosUserPrincipal principal, 
                                          @Argument UUID userId, @Argument BigDecimal newSalary, @Argument String reason, @Argument String effectiveDate) {
        return compensationService.updateSalary(userId, newSalary, reason, LocalDate.parse(effectiveDate), principal.getUserId());
    }
}
