package com.safo.icos.modules.compensation.infrastructure.repository.jpa;

import com.safo.icos.modules.compensation.infrastructure.repository.entity.BonusJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface BonusJpaRepository extends JpaRepository<BonusJpaEntity, UUID> {
    List<BonusJpaEntity> findByUserIdAndPaymentDateBetween(UUID userId, java.time.LocalDate startDate, java.time.LocalDate endDate);
}
