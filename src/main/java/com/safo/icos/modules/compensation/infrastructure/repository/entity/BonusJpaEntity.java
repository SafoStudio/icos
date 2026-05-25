package com.safo.icos.modules.compensation.infrastructure.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "bonuses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BonusJpaEntity {
    @Id
    private UUID id;
    
    @Column(name = "user_id")
    private UUID userId;
    
    private BigDecimal amount;
    private String type;
    private String description;
    private String status;
    private LocalDate paymentDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
