package com.safo.icos.modules.performance.infrastructure.repository.jpa;

import com.safo.icos.modules.performance.infrastructure.repository.entity.ReviewCycleJpaEntity;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewCycleJpaRepository extends JpaRepository<ReviewCycleJpaEntity, UUID> {
}
