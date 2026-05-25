package com.safo.icos.modules.performance.api.dto;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class PerformanceReviewDto {
    private final UUID id;
    private final Integer rating;
    private final String summary;
    private final String status;
}
