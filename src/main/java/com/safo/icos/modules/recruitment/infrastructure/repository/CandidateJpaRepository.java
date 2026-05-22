package com.safo.icos.modules.recruitment.infrastructure.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CandidateJpaRepository extends JpaRepository<CandidateJpaEntity, UUID> {
}
