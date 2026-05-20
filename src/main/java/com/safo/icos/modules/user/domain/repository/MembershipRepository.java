package com.safo.icos.modules.user.domain.repository;

import com.safo.icos.modules.user.domain.model.Membership;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MembershipRepository {
    Optional<Membership> findById(UUID id);
    Optional<Membership> findActiveByUserIdAndCompanyId(UUID userId, UUID companyId);
    Optional<Membership> findFirstByUserIdAndActiveTrue(UUID userId);
    List<Membership> findByUserId(UUID userId);
    Membership save(Membership membership);
    void delete(Membership membership);
}