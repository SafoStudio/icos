package com.safo.icos.modules.org.infrastructure.repository.jpa;

import com.safo.icos.modules.org.infrastructure.repository.entity.PositionJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionJpaRepository extends JpaRepository<PositionJpaEntity, UUID> {
  List<PositionJpaEntity> findByCompanyId(UUID companyId);

  boolean existsByCompanyIdAndName(UUID companyId, String name);
}
