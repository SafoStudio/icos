package com.safo.icos.modules.timeoff.domain.model;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

/**
 * Domain entity representing a type of leave (e.g., vacation, sick leave).
 */
@Getter
@Builder
public class LeaveType {
  private final UUID id;
  private final String name;
  private final String code;
  /** Whether this leave type deducts from the employee's balance. */
  private final boolean requiresBalance;
  private final String color;
  private final boolean isActive;
}