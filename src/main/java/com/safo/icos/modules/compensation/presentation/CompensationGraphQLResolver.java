package com.safo.icos.modules.compensation.presentation;

import com.safo.icos.modules.compensation.api.dto.CompensationDto;
import com.safo.icos.modules.compensation.api.dto.SalaryRevisionDto;
import com.safo.icos.modules.compensation.application.CompensationService;
import com.safo.icos.modules.compensation.infrastructure.mapper.CompensationMapper;
import com.safo.icos.security.context.IcosUserPrincipal;
import java.math.BigDecimal;
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
public class CompensationGraphQLResolver {

    private final CompensationService compensationService;
    private final CompensationMapper mapper;

    @QueryMapping
    public CompensationDto compensation(@Argument UUID userId) {
        return mapper.toDto(compensationService.getCompensation(userId));
    }

    @QueryMapping
    public List<SalaryRevisionDto> salaryHistory(@Argument UUID userId) {
        return compensationService.getSalaryHistory(userId).stream().map(mapper::toDto).toList();
    }

    @MutationMapping
    public CompensationDto updateSalary(@AuthenticationPrincipal IcosUserPrincipal principal,
                                          @Argument UUID userId, @Argument BigDecimal newSalary, @Argument String reason, @Argument String effectiveDate) {
        return mapper.toDto(compensationService.updateSalary(userId, newSalary, reason, LocalDate.parse(effectiveDate), principal.getUserId()));
    }
}
