package com.safo.icos.modules.invite.domain.repository;

import com.safo.icos.modules.invite.domain.model.Invite;
import java.util.Optional;
import java.util.UUID;

public interface InviteRepository {
  Invite save(Invite invite);

  Optional<Invite> findById(UUID id);

  Optional<Invite> findByToken(String token);

  boolean existsByEmail(String email);
}
