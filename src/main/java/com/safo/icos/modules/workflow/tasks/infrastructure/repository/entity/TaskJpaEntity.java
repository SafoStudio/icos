package com.safo.icos.modules.workflow.tasks.infrastructure.repository.entity;

import com.safo.icos.modules.workflow.tasks.domain.model.enums.TaskStatus;
import jakarta.persistence.*;
import lombok.*;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskJpaEntity {
    @Id
    private UUID id;
    private String title;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    private UUID assigneeId;
    private UUID departmentId;

    @ElementCollection
    @CollectionTable(name = "task_metadata", joinColumns = @JoinColumn(name = "task_id"))
    @MapKeyColumn(name = "key")
    @Column(name = "value")
    private Map<String, String> metadata;
}
