package com.safo.icos.modules.identity.invite.infrastructure.repository;

import com.safo.icos.modules.identity.invite.domain.model.Invite;
import com.safo.icos.modules.identity.invite.domain.repository.InviteRepository;
import com.safo.icos.modules.identity.invite.infrastructure.mapper.InviteMapper;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

/**
 * JPA-based implementation of {@link InviteRepository}.
 * Adapts domain persistence operations to Spring Data JPA.
 */
@Repository
@RequiredArgsConstructor
public class InviteRepositoryAdapter implements InviteRepository {
  private final InviteJpaRepository jpaRepository;
  private final InviteMapper mapper;

  @Override
  public Invite save(Invite invite) {
    InviteJpaEntity entity = mapper.toEntity(invite);
    InviteJpaEntity saved = jpaRepository.save(entity);
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Invite> findById(UUID id) {
    return jpaRepository.findById(id).map(mapper::toDomain);
  }

  @Override
  public Optional<Invite> findByToken(String token) {
    return jpaRepository.findByToken(token).map(mapper::toDomain);
  }

  @Override
  public boolean existsByEmail(String email) {
    return jpaRepository.existsByEmail(email);
  }
}
