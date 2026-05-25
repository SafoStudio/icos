package com.safo.icos.modules.timeoff.domain.model;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder(toBuilder = true)
public class LeaveBalance {
    private final UUID id;
    private final UUID userId;
    private final UUID leaveTypeId;
    private final BigDecimal totalDays;
    private final BigDecimal usedDays;
    private final BigDecimal remainingDays;

    public LeaveBalance deduct(BigDecimal days) {
        return this.toBuilder()
                .usedDays(this.usedDays.add(days))
                .remainingDays(this.remainingDays.subtract(days))
                .build();
    }

    public LeaveBalance restore(BigDecimal days) {
        return this.toBuilder()
                .usedDays(this.usedDays.subtract(days))
                .remainingDays(this.remainingDays.add(days))
                .build();
    }
}
