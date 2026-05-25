package com.safo.icos.modules.performance.domain.repository;

import com.safo.icos.modules.performance.domain.model.Objective;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ObjectiveRepository {
    Objective save(Objective objective);
    Optional<Objective> findById(UUID id);
    List<Objective> findByUserId(UUID userId);
}
