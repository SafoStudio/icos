package com.safo.icos.modules.performance.api.dto;

import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
public class ObjectiveDto {
    private final UUID id;
    private final String title;
    private final BigDecimal progress;
    private final String status;
}
