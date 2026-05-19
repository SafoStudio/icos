package com.safo.icos.modules.auth.infrastructure.repository;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "auth_credentials")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthCredentialsJpaEntity {
    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @Version
    private Long version;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
