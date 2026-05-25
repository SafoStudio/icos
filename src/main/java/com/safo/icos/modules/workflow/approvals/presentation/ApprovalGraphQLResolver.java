package com.safo.icos.modules.workflow.approvals.presentation;

import com.safo.icos.modules.workflow.approvals.application.ApprovalService;
import com.safo.icos.modules.workflow.approvals.domain.model.ApprovalRequest;
import com.safo.icos.security.context.IcosUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class ApprovalGraphQLResolver {

    private final ApprovalService approvalService;

    @QueryMapping
    public List<ApprovalRequest> myApprovalRequests(@AuthenticationPrincipal IcosUserPrincipal principal) {
        return approvalService.getApprovalsForUser(principal.getUserId());
    }

    @MutationMapping
    public ApprovalRequest approveRequest(@AuthenticationPrincipal IcosUserPrincipal principal, @Argument UUID id) {
        return approvalService.approveRequest(id, principal.getUserId());
    }

    @MutationMapping
    public ApprovalRequest rejectRequest(@AuthenticationPrincipal IcosUserPrincipal principal, @Argument UUID id) {
        return approvalService.rejectRequest(id, principal.getUserId());
    }
}
