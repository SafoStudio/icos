package com.safo.icos.modules.org.infrastructure.repository.adapter;

import com.safo.icos.modules.org.domain.model.Position;
import com.safo.icos.modules.org.domain.repository.PositionRepository;
import com.safo.icos.modules.org.infrastructure.mapper.OrgMapper;
import com.safo.icos.modules.org.infrastructure.repository.entity.PositionJpaEntity;
import com.safo.icos.modules.org.infrastructure.repository.jpa.PositionJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PositionRepositoryImpl implements PositionRepository {
  private final PositionJpaRepository jpaRepository;
  private final OrgMapper mapper;

  @Override
  public Optional<Position> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public List<Position> findByCompanyId(UUID companyId) {
    return jpaRepository.findByCompanyId(companyId).stream()
            .map(mapper::toDomain)
            .collect(Collectors.toList());
  }

  @Override
  public boolean existsByCompanyIdAndName(UUID companyId, String name) {
    return jpaRepository.existsByCompanyIdAndName(companyId, name);
  }

  @Override
  public Position save(Position position) {
    PositionJpaEntity entity = mapper.toJpa(position);
    PositionJpaEntity saved = jpaRepository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public void deleteById(UUID id) {
    jpaRepository.deleteById(id);
  }
}
