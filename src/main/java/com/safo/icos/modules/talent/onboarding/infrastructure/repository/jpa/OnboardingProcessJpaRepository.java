package com.safo.icos.modules.talent.onboarding.infrastructure.repository.jpa;

import com.safo.icos.modules.talent.onboarding.infrastructure.repository.entity.OnboardingProcessJpaEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OnboardingProcessJpaRepository extends JpaRepository<OnboardingProcessJpaEntity, UUID> {
    Optional<OnboardingProcessJpaEntity> findByMembershipId(UUID membershipId);

    @Query(value = "SELECT p.* FROM onboarding_processes p JOIN memberships m ON p.membership_id = m.id WHERE m.company_id = :companyId", nativeQuery = true)
    List<OnboardingProcessJpaEntity> findByCompanyId(@Param("companyId") UUID companyId);
}
