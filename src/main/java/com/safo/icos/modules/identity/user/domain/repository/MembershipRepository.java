package com.safo.icos.modules.identity.user.domain.repository;

import com.safo.icos.modules.identity.user.domain.model.Membership;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for {@link Membership} persistence.
 */
public interface MembershipRepository {

  Optional<Membership> findById(UUID id);

  /** Finds the active membership for a user in a specific company. */
  Optional<Membership> findActiveByUserIdAndCompanyId(UUID userId, UUID companyId);

  /** Returns all memberships in a company. */
  List<Membership> findAllByCompanyId(UUID companyId);

  /** Returns the first active membership for a user across all companies. */
  Optional<Membership> findFirstByUserIdAndActiveTrue(UUID userId);

  /** Returns all memberships for a user. */
  List<Membership> findByUserId(UUID userId);

  Membership save(Membership membership);

  void deleteById(UUID id);
}
