package com.safo.icos.modules.timeoff.domain.model;

import com.safo.icos.modules.timeoff.domain.model.enums.LeaveRequestStatus;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class LeaveRequest {
    private final UUID id;
    private final UUID userId;
    private final UUID leaveTypeId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal totalDays;
    private final String reason;
    private final LeaveRequestStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;
    private final LocalDateTime approvedAt;
    private final LocalDateTime rejectedAt;
    private final LocalDateTime cancelledAt;

    public static LeaveRequest create(UUID userId, UUID leaveTypeId, LocalDate startDate, LocalDate endDate, BigDecimal totalDays, String reason) {
        return LeaveRequest.builder()
                .id(UUID.randomUUID())
                .userId(userId)
                .leaveTypeId(leaveTypeId)
                .startDate(startDate)
                .endDate(endDate)
                .totalDays(totalDays)
                .reason(reason)
                .status(LeaveRequestStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public LeaveRequest approve() {
        return this.toBuilder()
                .status(LeaveRequestStatus.APPROVED)
                .approvedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public LeaveRequest reject() {
        return this.toBuilder()
                .status(LeaveRequestStatus.REJECTED)
                .rejectedAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public LeaveRequest cancel() {
        return this.toBuilder()
                .status(LeaveRequestStatus.CANCELLED)
                .cancelledAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }
}
