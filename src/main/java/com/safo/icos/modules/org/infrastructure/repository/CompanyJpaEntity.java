package com.safo.icos.modules.org.infrastructure.repository;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "companies")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyJpaEntity {
    @Id
    private UUID id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "tax_id", unique = true)
    private String taxId;

    @Column(name = "policy_version", nullable = false)
    private Integer policyVersion;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
