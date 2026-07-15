package com.safo.icos.modules.compensation.domain.repository;

import com.safo.icos.modules.compensation.domain.model.CompensationRecord;
import com.safo.icos.modules.compensation.domain.model.SalaryRevision;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompensationRepository {
    CompensationRecord save(CompensationRecord record);
    Optional<CompensationRecord> findByUserId(UUID userId);

    SalaryRevision saveRevision(SalaryRevision revision);
    List<SalaryRevision> findRevisionsByUserId(UUID userId);
}
