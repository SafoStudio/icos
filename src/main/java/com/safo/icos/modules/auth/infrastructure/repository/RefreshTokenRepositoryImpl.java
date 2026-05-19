// modules/auth/infrastructure/repository/RefreshTokenRepositoryImpl.java
package com.safo.icos.modules.auth.infrastructure.repository;

import com.safo.icos.modules.auth.domain.model.RefreshToken;
import com.safo.icos.modules.auth.domain.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class RefreshTokenRepositoryImpl implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    @Override
    public RefreshToken save(RefreshToken token) {
        RefreshTokenJpaEntity entity = toJpaEntity(token);
        RefreshTokenJpaEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByTokenHash(String tokenHash) {
        return jpaRepository.findByTokenHash(tokenHash).map(this::toDomain);
    }

    @Override
    public Optional<RefreshToken> findById(UUID id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public void delete(RefreshToken token) {
        jpaRepository.deleteById(token.getId());
    }

    @Override
    public void deleteByMembershipId(UUID membershipId) {
        jpaRepository.deleteByMembershipId(membershipId);
    }

    private RefreshTokenJpaEntity toJpaEntity(RefreshToken domain) {
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

    private RefreshToken toDomain(RefreshTokenJpaEntity entity) {
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