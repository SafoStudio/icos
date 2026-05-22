package com.safo.icos.modules.auth.domain.repository;

import com.safo.icos.modules.auth.domain.model.RefreshToken;
import com.safo.icos.modules.auth.domain.model.TokenValue;
import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository {
  RefreshToken save(RefreshToken token);

  Optional<RefreshToken> findByTokenHash(TokenValue tokenHash);

  Optional<RefreshToken> findById(UUID id);

  void delete(RefreshToken token);

  void deleteByMembershipId(UUID membershipId);
}
