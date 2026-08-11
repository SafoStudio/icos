package com.safo.icos.modules.compensation.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Bonus {
  private final UUID id;
  private final UUID userId;
  private final BigDecimal amount;
  private final String type;
  private final String description;
  private final String status;
  private final LocalDate paymentDate;

  public static Bonus create(UUID userId, BigDecimal amount, String type,
                             String description, LocalDate paymentDate) {
    return Bonus.builder()
            .id(UUID.randomUUID()).userId(userId).amount(amount)
            .type(type).description(description).status("PENDING")
            .paymentDate(paymentDate).build();
  }
}