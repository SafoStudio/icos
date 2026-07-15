package com.safo.icos.modules.talent.recruitment.domain.repository;

import com.safo.icos.modules.talent.recruitment.domain.model.Candidate;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for {@link Candidate} persistence.
 */
public interface CandidateRepository {
  Candidate save(Candidate candidate);
  Optional<Candidate> findById(UUID id);
  void delete(UUID id);
}
