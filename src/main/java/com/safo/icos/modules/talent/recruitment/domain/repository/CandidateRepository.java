package com.safo.icos.modules.recruitment.domain.repository;

import com.safo.icos.modules.recruitment.domain.model.Candidate;
import java.util.Optional;
import java.util.UUID;

public interface CandidateRepository {
    Candidate save(Candidate candidate);
    Optional<Candidate> findById(UUID id);
    void delete(UUID id);
}
