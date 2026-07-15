package com.safo.icos.modules.timeoff.presentation;

import com.safo.icos.modules.timeoff.api.dto.LeaveRequestDto;
import com.safo.icos.modules.timeoff.application.LeaveService;
import com.safo.icos.modules.timeoff.domain.model.LeaveBalance;
import com.safo.icos.modules.timeoff.infrastructure.mapper.TimeOffMapper;
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
public class LeaveGraphQLResolver {

    private final LeaveService leaveService;
    private final TimeOffMapper mapper;

    @QueryMapping
    public List<LeaveRequestDto> myLeaveRequests(@AuthenticationPrincipal IcosUserPrincipal principal) {
        return leaveService.getMyRequests(principal.getUserId()).stream().map(mapper::toDto).toList();
    }

    @QueryMapping
    public List<LeaveBalance> myLeaveBalances(@AuthenticationPrincipal IcosUserPrincipal principal) {
        return leaveService.getMyBalances(principal.getUserId());
    }

    @MutationMapping
    public LeaveRequestDto createLeaveRequest(@AuthenticationPrincipal IcosUserPrincipal principal,
                                           @Argument UUID leaveTypeId, @Argument String startDate, @Argument String endDate, @Argument String reason) {
        return mapper.toDto(leaveService.createRequest(principal.getUserId(), leaveTypeId, LocalDate.parse(startDate), LocalDate.parse(endDate), reason));
    }

    @MutationMapping
    public LeaveRequestDto approveLeaveRequest(@AuthenticationPrincipal IcosUserPrincipal principal, @Argument UUID id) {
        return mapper.toDto(leaveService.approveRequest(id, principal.getUserId()));
    }

    @MutationMapping
    public LeaveRequestDto rejectLeaveRequest(@AuthenticationPrincipal IcosUserPrincipal principal, @Argument UUID id, @Argument String comment) {
        return mapper.toDto(leaveService.rejectRequest(id, principal.getUserId(), comment));
    }

    @MutationMapping
    public LeaveRequestDto cancelLeaveRequest(@AuthenticationPrincipal IcosUserPrincipal principal, @Argument UUID id) {
        return mapper.toDto(leaveService.cancelRequest(id, principal.getUserId()));
    }
    }
