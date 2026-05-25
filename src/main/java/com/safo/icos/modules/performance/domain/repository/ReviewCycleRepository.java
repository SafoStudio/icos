package com.safo.icos.modules.performance.domain.repository;

import com.safo.icos.modules.performance.domain.model.ReviewCycle;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReviewCycleRepository {
    ReviewCycle save(ReviewCycle cycle);
    Optional<ReviewCycle> findById(UUID id);
    List<ReviewCycle> findAll();
}
