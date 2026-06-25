package com.safo.icos.modules.compensation.domain.model;

import com.safo.icos.modules.compensation.domain.model.enums.PayrollStatus;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PayrollPeriod {
    private final UUID id;
    private final String name;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final PayrollStatus status;

    public static PayrollPeriod create(String name, LocalDate start, LocalDate end) {
        return PayrollPeriod.builder()
                .id(UUID.randomUUID())
                .name(name)
                .startDate(start)
                .endDate(end)
                .status(PayrollStatus.DRAFT)
                .build();
    }
}
