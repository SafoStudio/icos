package com.safo.icos.modules.performance.api.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PerformanceReviewDto {
    private final UUID id;
    private final Integer rating;
    private final String summary;
    private final String status;
}
