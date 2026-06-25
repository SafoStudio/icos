package com.safo.icos.modules.timeoff.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LeaveRequestDto {
    private final UUID id;
    private final UUID userId;
    private final UUID leaveTypeId;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final BigDecimal totalDays;
    private final String status;
    private final String reason;
}
