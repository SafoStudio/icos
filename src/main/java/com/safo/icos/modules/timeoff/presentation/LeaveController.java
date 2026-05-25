package com.safo.icos.modules.timeoff.presentation;

import com.safo.icos.modules.timeoff.application.LeaveService;
import com.safo.icos.modules.timeoff.domain.model.LeaveBalance;
import com.safo.icos.modules.timeoff.domain.model.LeaveRequest;
import com.safo.icos.security.context.IcosUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/time-off")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @PostMapping("/requests")
    public ResponseEntity<LeaveRequest> createRequest(
            @AuthenticationPrincipal IcosUserPrincipal principal,
            @RequestParam UUID leaveTypeId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(leaveService.createRequest(principal.getUserId(), leaveTypeId, startDate, endDate, reason));
    }

    @GetMapping("/requests/me")
    public ResponseEntity<List<LeaveRequest>> getMyRequests(@AuthenticationPrincipal IcosUserPrincipal principal) {
        return ResponseEntity.ok(leaveService.getMyRequests(principal.getUserId()));
    }

    @GetMapping("/balances/me")
    public ResponseEntity<List<LeaveBalance>> getMyBalances(@AuthenticationPrincipal IcosUserPrincipal principal) {
        return ResponseEntity.ok(leaveService.getMyBalances(principal.getUserId()));
    }

    @PostMapping("/requests/{id}/approve")
    public ResponseEntity<LeaveRequest> approveRequest(
            @AuthenticationPrincipal IcosUserPrincipal principal,
            @PathVariable UUID id) {
        return ResponseEntity.ok(leaveService.approveRequest(id, principal.getUserId()));
    }

    @PostMapping("/requests/{id}/reject")
    public ResponseEntity<LeaveRequest> rejectRequest(
            @AuthenticationPrincipal IcosUserPrincipal principal,
            @PathVariable UUID id,
            @RequestParam(required = false) String comment) {
        return ResponseEntity.ok(leaveService.rejectRequest(id, principal.getUserId(), comment));
    }

    @PostMapping("/requests/{id}/cancel")
    public ResponseEntity<LeaveRequest> cancelRequest(
            @AuthenticationPrincipal IcosUserPrincipal principal,
            @PathVariable UUID id) {
        return ResponseEntity.ok(leaveService.cancelRequest(id, principal.getUserId()));
    }
}
