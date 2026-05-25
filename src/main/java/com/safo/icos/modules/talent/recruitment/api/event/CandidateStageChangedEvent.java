package com.safo.icos.modules.recruitment.api.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CandidateStageChangedEvent extends ApplicationEvent {
    private final UUID candidateId;
    private final UUID applicationId;
    private final UUID stageId;
    private final UUID userId; // The owner/manager of the application or the candidate themselves

    public CandidateStageChangedEvent(Object source, UUID candidateId, UUID applicationId, UUID stageId, UUID userId) {
        super(source);
        this.candidateId = candidateId;
        this.applicationId = applicationId;
        this.stageId = stageId;
        this.userId = userId;
    }
}
