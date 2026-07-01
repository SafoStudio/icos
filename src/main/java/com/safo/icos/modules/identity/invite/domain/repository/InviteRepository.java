package com.safo.icos.modules.identity.invite.domain.repository;

import com.safo.icos.modules.identity.invite.domain.model.Invite;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for {@link Invite} persistence.
 * Implemented in the infrastructure layer by {@code InviteRepositoryImpl}.
 */
public interface InviteRepository {

  /**
   * Persists a new or updated invite.
   *
   * @param invite the domain model to save
   * @return the saved invite with any generated values
   */
  Invite save(Invite invite);

  /**
   * Finds an invite by its unique identifier.
   *
   * @param id the invite UUID
   * @return the invite if found
   */
  Optional<Invite> findById(UUID id);

  /**
   * Finds an invite by its hashed token value.
   *
   * @param token the hashed token string
   * @return the invite if found
   */
  Optional<Invite> findByToken(String token);

  /**
   * Checks if an invite already exists for the given email.
   *
   * @param email the invitee's email address
   * @return {@code true} if an invite for this email exists
   */
  boolean existsByEmail(String email);
}