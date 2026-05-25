package com.safo.icos.modules.timeoff.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "leave_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveTypeJpaEntity {
    @Id
    private UUID id;
    private String name;
    private String code;
    private boolean requiresBalance;
    private String color;
    private boolean isActive;
}
