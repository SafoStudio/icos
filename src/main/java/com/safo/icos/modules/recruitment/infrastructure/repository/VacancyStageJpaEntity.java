package com.safo.icos.modules.recruitment.infrastructure.repository;

import com.safo.icos.modules.recruitment.domain.model.PipelineStageType;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;

@Entity
@Table(name = "vacancy_stages")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VacancyStageJpaEntity {
    @Id
    private UUID id;

    @Column(name = "vacancy_id", nullable = false)
    private UUID vacancyId;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PipelineStageType type;

    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
}
