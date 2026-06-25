package com.safo.icos.modules.performance.infrastructure.mapper;

import com.safo.icos.modules.performance.api.dto.ObjectiveDto;
import com.safo.icos.modules.performance.api.dto.PerformanceReviewDto;
import com.safo.icos.modules.performance.domain.model.KeyResult;
import com.safo.icos.modules.performance.domain.model.Objective;
import com.safo.icos.modules.performance.domain.model.PerformanceReview;
import com.safo.icos.modules.performance.domain.model.ReviewCycle;
import com.safo.icos.modules.performance.infrastructure.repository.entity.KeyResultJpaEntity;
import com.safo.icos.modules.performance.infrastructure.repository.entity.ObjectiveJpaEntity;
import com.safo.icos.modules.performance.infrastructure.repository.entity.PerformanceReviewJpaEntity;
import com.safo.icos.modules.performance.infrastructure.repository.entity.ReviewCycleJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class PerformanceMapper {

    public Objective toDomain(ObjectiveJpaEntity entity) {
        if (entity == null) return null;
        return Objective.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .title(entity.getTitle())
                .description(entity.getDescription())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .progress(entity.getProgress())
                .status(entity.getStatus())
                .build();
    }

    public ObjectiveJpaEntity toEntity(Objective domain) {
        if (domain == null) return null;
        return ObjectiveJpaEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .title(domain.getTitle())
                .description(domain.getDescription())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .progress(domain.getProgress())
                .status(domain.getStatus())
                .build();
    }

    public KeyResult toDomain(KeyResultJpaEntity entity) {
        if (entity == null) return null;
        return KeyResult.builder()
                .id(entity.getId())
                .objectiveId(entity.getObjectiveId())
                .title(entity.getTitle())
                .targetValue(entity.getTargetValue())
                .currentValue(entity.getCurrentValue())
                .unit(entity.getUnit())
                .weight(entity.getWeight())
                .build();
    }

    public KeyResultJpaEntity toEntity(KeyResult domain) {
        if (domain == null) return null;
        return KeyResultJpaEntity.builder()
                .id(domain.getId())
                .objectiveId(domain.getObjectiveId())
                .title(domain.getTitle())
                .targetValue(domain.getTargetValue())
                .currentValue(domain.getCurrentValue())
                .unit(domain.getUnit())
                .weight(domain.getWeight())
                .build();
    }

    public ReviewCycle toDomain(ReviewCycleJpaEntity entity) {
        if (entity == null) return null;
        return ReviewCycle.builder()
                .id(entity.getId())
                .name(entity.getName())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .status(entity.getStatus())
                .build();
    }

    public ReviewCycleJpaEntity toEntity(ReviewCycle domain) {
        if (domain == null) return null;
        return ReviewCycleJpaEntity.builder()
                .id(domain.getId())
                .name(domain.getName())
                .startDate(domain.getStartDate())
                .endDate(domain.getEndDate())
                .status(domain.getStatus())
                .build();
    }

    public PerformanceReview toDomain(PerformanceReviewJpaEntity entity) {
        if (entity == null) return null;
        return PerformanceReview.builder()
                .id(entity.getId())
                .cycleId(entity.getCycleId())
                .employeeId(entity.getEmployeeId())
                .managerId(entity.getManagerId())
                .rating(entity.getRating())
                .summary(entity.getSummary())
                .status(entity.getStatus())
                .finalizedAt(entity.getFinalizedAt())
                .build();
    }

    public PerformanceReviewJpaEntity toEntity(PerformanceReview domain) {
        if (domain == null) return null;
        return PerformanceReviewJpaEntity.builder()
                .id(domain.getId())
                .cycleId(domain.getCycleId())
                .employeeId(domain.getEmployeeId())
                .managerId(domain.getManagerId())
                .rating(domain.getRating())
                .summary(domain.getSummary())
                .status(domain.getStatus())
                .finalizedAt(domain.getFinalizedAt())
                .build();
    }

    public ObjectiveDto toDto(Objective domain) {
        if (domain == null) return null;
        return ObjectiveDto.builder()
                .id(domain.getId())
                .title(domain.getTitle())
                .progress(domain.getProgress())
                .status(domain.getStatus().name())
                .build();
    }

    public PerformanceReviewDto toDto(PerformanceReview domain) {
        if (domain == null) return null;
        return PerformanceReviewDto.builder()
                .id(domain.getId())
                .rating(domain.getRating())
                .summary(domain.getSummary())
                .status(domain.getStatus())
                .build();
    }
}
