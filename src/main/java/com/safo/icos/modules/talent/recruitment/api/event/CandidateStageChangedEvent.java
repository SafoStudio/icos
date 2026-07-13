package com.safo.icos.modules.talent.recruitment.api.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * Published when a candidate moves to a different recruitment stage.
 * Used for notifications and audit logging.
 */
@Getter
public class CandidateStageChangedEvent extends ApplicationEvent {
  private final UUID candidateId;
  private final UUID applicationId;
  private final UUID stageId;
  /** The user who changed the stage. */
  private final UUID userId;

  public CandidateStageChangedEvent(Object source, UUID candidateId,
                                    UUID applicationId, UUID stageId, UUID userId) {
    super(source);
    this.candidateId = candidateId;
    this.applicationId = applicationId;
    this.stageId = stageId;
    this.userId = userId;
  }
}