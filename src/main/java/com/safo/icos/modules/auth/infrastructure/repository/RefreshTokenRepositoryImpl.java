package com.safo.icos.modules.auth.infrastructure.repository;

import com.safo.icos.modules.auth.domain.model.RefreshToken;
import com.safo.icos.modules.auth.domain.model.TokenValue;
import com.safo.icos.modules.auth.domain.repository.RefreshTokenRepository;
import com.safo.icos.modules.auth.infrastructure.mapper.RefreshTokenMapper;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
    return jpaRepository.findByTokenHash(tokenHash.value()).map(refreshTokenMapper::toDomain);
  }

  @Override
  public Optional<RefreshToken> findById(UUID id) {
    return jpaRepository.findById(id).map(refreshTokenMapper::toDomain);
  }

  @Override
  public void delete(RefreshToken token) {
    jpaRepository.deleteById(token.getId().value());
  }

  @Override
  public void deleteByMembershipId(UUID membershipId) {
    jpaRepository.deleteByMembershipId(membershipId);
  }
}
