package com.safo.icos.modules.talent.recruitment.api.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published when a candidate is hired.
 * Triggers downstream processes such as onboarding and user creation.
 */
@Getter
public class CandidateHiredEvent extends ApplicationEvent {
  private final UUID candidateId;
  private final UUID vacancyId;
  private final UUID companyId;
  /** The newly created user ID for the hired candidate. */
  private final UUID userId;

  public CandidateHiredEvent(Object source, UUID candidateId, UUID vacancyId,
                             UUID companyId, UUID userId) {
    super(source);
    this.candidateId = candidateId;
    this.vacancyId = vacancyId;
    this.companyId = companyId;
    this.userId = userId;
  }
}
