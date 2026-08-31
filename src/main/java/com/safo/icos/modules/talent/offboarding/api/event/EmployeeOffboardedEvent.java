package com.safo.icos.modules.talent.offboarding.api.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published when an employee is fully offboarded.
 * Triggers: deactivation, account cleanup, etc.
 */
@Getter
public class EmployeeOffboardedEvent extends ApplicationEvent {
  private final UUID membershipId;
  private final UUID userId;
  private final UUID companyId;

  public EmployeeOffboardedEvent(Object source, UUID membershipId, UUID userId, UUID companyId) {
    super(source);
    this.membershipId = membershipId;
    this.userId = userId;
    this.companyId = companyId;
  }
}