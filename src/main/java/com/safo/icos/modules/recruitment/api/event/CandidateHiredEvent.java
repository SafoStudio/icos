package com.safo.icos.modules.recruitment.api.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CandidateHiredEvent extends ApplicationEvent {
    private final UUID candidateId;
    private final UUID vacancyId;
    private final UUID companyId;
    private final UUID userId; // The User ID of the newly hired candidate

    public CandidateHiredEvent(Object source, UUID candidateId, UUID vacancyId, UUID companyId, UUID userId) {
        super(source);
        this.candidateId = candidateId;
        this.vacancyId = vacancyId;
        this.companyId = companyId;
        this.userId = userId;
    }
}
