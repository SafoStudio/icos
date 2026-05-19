package com.safo.icos.modules.auth.infrastructure.mapper;

import com.safo.icos.modules.auth.domain.model.RefreshToken;
import com.safo.icos.modules.auth.infrastructure.repository.RefreshTokenJpaEntity;
import org.springframework.stereotype.Component;
import java.time.Instant;

@Component
public class RefreshTokenMapper {

    public RefreshTokenJpaEntity toJpaEntity(RefreshToken domain) {
        if (domain == null) return null;

        return RefreshTokenJpaEntity.builder()
                .id(domain.getId())
                .membershipId(domain.getMembershipId())
                .tokenHash(domain.getTokenHash())
                .deviceInfo(domain.getDeviceInfo())
                .expiresAt(domain.getExpiresAt())
                .revoked(domain.isRevoked())
                .createdAt(domain.getCreatedAt())
                .updatedAt(Instant.now())
                .build();
    }

    public RefreshToken toDomain(RefreshTokenJpaEntity entity) {
        if (entity == null) return null;

        return RefreshToken.builder()
                .id(entity.getId())
                .membershipId(entity.getMembershipId())
                .tokenHash(entity.getTokenHash())
                .deviceInfo(entity.getDeviceInfo())
                .createdAt(entity.getCreatedAt())
                .expiresAt(entity.getExpiresAt())
                .revoked(entity.isRevoked())
                .revokedAt(null)
                .build();
    }
}