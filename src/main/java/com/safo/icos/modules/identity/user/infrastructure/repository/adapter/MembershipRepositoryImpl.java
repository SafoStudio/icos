package com.safo.icos.modules.identity.user.infrastructure.repository.adapter;

import com.safo.icos.modules.identity.user.domain.model.Membership;
import com.safo.icos.modules.identity.user.domain.repository.MembershipRepository;
import com.safo.icos.modules.identity.user.infrastructure.repository.entity.MembershipJpaEntity;
import com.safo.icos.modules.identity.user.infrastructure.repository.jpa.MembershipJpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class MembershipRepositoryImpl implements MembershipRepository {

  private final MembershipJpaRepository jpaRepository;

  @Override
  public Optional<Membership> findById(UUID id) {
    return jpaRepository.findById(id).map(this::toDomain);
  }

  @Override
  public Optional<Membership> findActiveByUserIdAndCompanyId(UUID userId, UUID companyId) {
    return jpaRepository.findActiveByUserIdAndCompanyId(userId, companyId).map(this::toDomain);
  }

  @Override
  public List<Membership> findAllByCompanyId(UUID companyId) {
    return jpaRepository.findAllByCompanyId(companyId).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
  }

  @Override
  public Optional<Membership> findFirstByUserIdAndActiveTrue(UUID userId) {
    return jpaRepository.findFirstByUserIdAndActiveTrue(userId).map(this::toDomain);
  }

  @Override
  public List<Membership> findByUserId(UUID userId) {
    return jpaRepository.findByUserId(userId).stream()
            .map(this::toDomain)
            .collect(Collectors.toList());
  }

  @Override
  public Membership save(Membership membership) {
    MembershipJpaEntity entity = toJpaEntity(membership);
    MembershipJpaEntity saved = jpaRepository.save(entity);
    return toDomain(saved);
  }

  @Override
  public void delete(Membership membership) {
    jpaRepository.deleteById(membership.getId());
  }

  private MembershipJpaEntity toJpaEntity(Membership domain) {
    return MembershipJpaEntity.builder()
            .id(domain.getId())
            .userId(domain.getUserId())
            .companyId(domain.getCompanyId())
            .roleId(domain.getRoleId())
            .departmentId(domain.getDepartmentId())
            .managerId(domain.getManagerId())
            .active(domain.isActive())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt())
            .build();
  }

  private Membership toDomain(MembershipJpaEntity entity) {
    return Membership.builder()
            .id(entity.getId())
            .userId(entity.getUserId())
            .companyId(entity.getCompanyId())
            .roleId(entity.getRoleId())
            .departmentId(entity.getDepartmentId())
            .managerId(entity.getManagerId())
            .active(entity.isActive())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
  }
}
