package com.safo.icos.modules.timeoff.presentation;

import com.safo.icos.modules.timeoff.api.dto.LeaveRequestDto;
import com.safo.icos.modules.timeoff.application.LeaveService;
import com.safo.icos.modules.timeoff.domain.model.LeaveBalance;
import com.safo.icos.modules.timeoff.domain.model.LeaveType;
import com.safo.icos.modules.timeoff.domain.repository.LeaveTypeRepository;
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
    private final LeaveTypeRepository typeRepository;

    @QueryMapping
    public List<LeaveRequestDto> myLeaveRequests(@AuthenticationPrincipal IcosUserPrincipal principal) {
        if (principal == null) return List.of();
        try {
            return leaveService.getMyRequests(principal.getUserId()).stream().map(mapper::toDto).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @QueryMapping
    public List<LeaveBalance> myLeaveBalances(@AuthenticationPrincipal IcosUserPrincipal principal) {
        if (principal == null) return List.of();
        try {
            return leaveService.getMyBalances(principal.getUserId());
        } catch (Exception e) {
            return List.of();
        }
    }

    @QueryMapping
    public List<LeaveRequestDto> userLeaveRequests(@Argument UUID userId) {
        try {
            return leaveService.getMyRequests(userId).stream().map(mapper::toDto).toList();
        } catch (Exception e) {
            return List.of();
        }
    }

    @QueryMapping
    public List<LeaveBalance> userLeaveBalances(@Argument UUID userId) {
        try {
            return leaveService.getMyBalances(userId);
        } catch (Exception e) {
            return List.of();
        }
    }

    @QueryMapping
    public List<LeaveType> leaveTypes() {
        try {
            return typeRepository.findAllActive();
        } catch (Exception e) {
            return List.of();
        }
    }

    @MutationMapping
    public LeaveRequestDto createLeaveRequest(@AuthenticationPrincipal IcosUserPrincipal principal,
                                           @Argument UUID leaveTypeId, @Argument String startDate, @Argument String endDate, @Argument String reason) {
        UUID userId = principal != null ? principal.getUserId() : UUID.randomUUID();
        return mapper.toDto(leaveService.createRequest(userId, leaveTypeId, LocalDate.parse(startDate), LocalDate.parse(endDate), reason));
    }

    @MutationMapping
    public LeaveRequestDto approveLeaveRequest(@AuthenticationPrincipal IcosUserPrincipal principal, @Argument UUID id) {
        UUID approverId = principal != null ? principal.getUserId() : UUID.randomUUID();
        return mapper.toDto(leaveService.approveRequest(id, approverId));
    }

    @MutationMapping
    public LeaveRequestDto rejectLeaveRequest(@AuthenticationPrincipal IcosUserPrincipal principal, @Argument UUID id, @Argument String comment) {
        UUID approverId = principal != null ? principal.getUserId() : UUID.randomUUID();
        return mapper.toDto(leaveService.rejectRequest(id, approverId, comment != null ? comment : "Rejected"));
    }

    @MutationMapping
    public LeaveRequestDto cancelLeaveRequest(@AuthenticationPrincipal IcosUserPrincipal principal, @Argument UUID id) {
        UUID userId = principal != null ? principal.getUserId() : UUID.randomUUID();
        return mapper.toDto(leaveService.cancelRequest(id, userId));
    }
}
