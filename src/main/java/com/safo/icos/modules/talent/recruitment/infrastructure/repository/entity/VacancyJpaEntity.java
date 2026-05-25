package com.safo.icos.modules.talent.recruitment.infrastructure.repository.entity;

import com.safo.icos.modules.talent.recruitment.domain.model.enums.VacancyStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "vacancies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacancyJpaEntity {
    @Id
    private UUID id;

    @Column(name = "company_id", nullable = false)
    private UUID companyId;

    @Column(name = "department_id")
    private UUID departmentId;

    @Column(name = "position_id")
    private UUID positionId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    private String requirements;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VacancyStatus status;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
