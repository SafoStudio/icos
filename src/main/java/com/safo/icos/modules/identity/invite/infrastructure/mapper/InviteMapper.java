package com.safo.icos.modules.identity.invite.infrastructure.mapper;

import com.safo.icos.modules.identity.invite.domain.model.Invite;
import com.safo.icos.modules.identity.invite.infrastructure.repository.InviteJpaEntity;
import org.springframework.stereotype.Component;

@Component
public class InviteMapper {
  public Invite toDomain(InviteJpaEntity entity) {
    if (entity == null) return null;
    return Invite.builder()
            .id(entity.getId())
            .email(entity.getEmail())
            .role(entity.getRole())
            .companyId(entity.getCompanyId())
            .departmentId(entity.getDepartmentId())
            .managerId(entity.getManagerId())
            .invitedById(entity.getInvitedById())
            .token(entity.getToken())
            .status(entity.getStatus())
            .expiresAt(entity.getExpiresAt())
            .createdAt(entity.getCreatedAt())
            .updatedAt(entity.getUpdatedAt())
            .build();
  }

  public InviteJpaEntity toEntity(Invite domain) {
    if (domain == null) return null;
    return InviteJpaEntity.builder()
            .id(domain.getId())
            .email(domain.getEmail())
            .role(domain.getRole())
            .companyId(domain.getCompanyId())
            .departmentId(domain.getDepartmentId())
            .managerId(domain.getManagerId())
            .invitedById(domain.getInvitedById())
            .token(domain.getToken())
            .status(domain.getStatus())
            .expiresAt(domain.getExpiresAt())
            .createdAt(domain.getCreatedAt())
            .updatedAt(domain.getUpdatedAt() != null ? domain.getUpdatedAt() : domain.getCreatedAt())
            .build();
  }
}
