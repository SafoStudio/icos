package com.safo.icos.modules.org.domain.repository;

import com.safo.icos.modules.org.domain.model.Position;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PositionRepository {
  Optional<Position> findById(UUID id);

  List<Position> findByCompanyId(UUID companyId);

  boolean existsByCompanyIdAndName(UUID companyId, String name);

  Position save(Position position);
}
