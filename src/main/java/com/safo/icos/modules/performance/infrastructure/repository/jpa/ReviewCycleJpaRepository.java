package com.safo.icos.modules.performance.infrastructure.repository.jpa;

import com.safo.icos.modules.performance.infrastructure.repository.entity.ReviewCycleJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReviewCycleJpaRepository extends JpaRepository<ReviewCycleJpaEntity, UUID> {
}
