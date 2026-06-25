package com.safo.icos.modules.performance.api.dto;

import java.math.BigDecimal;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ObjectiveDto {
    private final UUID id;
    private final String title;
    private final BigDecimal progress;
    private final String status;
}
