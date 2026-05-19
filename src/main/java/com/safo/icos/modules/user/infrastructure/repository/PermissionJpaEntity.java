package com.safo.icos.modules.user.infrastructure.repository;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "permissions")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionJpaEntity {
    @Id
    private UUID id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "version", nullable = false)
    private Integer version;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
