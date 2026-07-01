package com.safo.icos.modules.identity.auth.infrastructure.repository.adapter;

import com.safo.icos.modules.identity.auth.domain.model.RefreshToken;
import com.safo.icos.modules.identity.auth.domain.model.TokenValue;
import com.safo.icos.modules.identity.auth.domain.repository.RefreshTokenRepository;
import com.safo.icos.modules.identity.auth.infrastructure.mapper.RefreshTokenMapper;
import com.safo.icos.modules.identity.auth.infrastructure.repository.jpa.RefreshTokenJpaRepository;
import com.safo.icos.modules.identity.auth.infrastructure.repository.entity.RefreshTokenJpaEntity;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * JPA-based implementation of {@link RefreshTokenRepository}.
 * Adapts domain persistence operations to Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

  private final RefreshTokenJpaRepository jpaRepository;
  private final RefreshTokenMapper refreshTokenMapper;

  @Override
  public RefreshToken save(RefreshToken token) {
    RefreshTokenJpaEntity entity = refreshTokenMapper.toJpaEntity(token);
    RefreshTokenJpaEntity saved = jpaRepository.save(entity);
    return refreshTokenMapper.toDomain(saved);
  }

  @Override
  public Optional<RefreshToken> findByTokenHash(TokenValue tokenHash) {
    return jpaRepository.findByTokenHash(tokenHash.hash()).map(refreshTokenMapper::toDomain);
  }

  @Override
  public Optional<RefreshToken> findById(UUID id) {
    return jpaRepository.findById(id).map(refreshTokenMapper::toDomain);
  }

  @Override
  public void delete(RefreshToken token) {
    jpaRepository.deleteById(token.getId().value());
  }
}
