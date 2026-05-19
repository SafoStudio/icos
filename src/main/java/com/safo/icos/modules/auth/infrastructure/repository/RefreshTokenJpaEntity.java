package com.safo.icos.modules.auth.infrastructure.repository;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenJpaEntity {
    @Id
    private UUID id;

    @Column(name = "membership_id", nullable = false)
    private UUID membershipId;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "device_info")
    private String deviceInfo;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "revoked", nullable = false)
    private boolean revoked;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
}