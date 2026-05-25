package com.safo.icos.modules.performance.domain.repository;

import com.safo.icos.modules.performance.domain.model.KeyResult;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface KeyResultRepository {
    KeyResult save(KeyResult keyResult);
    Optional<KeyResult> findById(UUID id);
    List<KeyResult> findByObjectiveId(UUID objectiveId);
}
