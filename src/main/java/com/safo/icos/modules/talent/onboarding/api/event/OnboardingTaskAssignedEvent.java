package com.safo.icos.modules.talent.onboarding.api.event;

import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class OnboardingTaskAssignedEvent extends ApplicationEvent {
    private final UUID taskId;
    private final UUID assigneeId;
    private final String taskTitle;

    public OnboardingTaskAssignedEvent(Object source, UUID taskId, UUID assigneeId, String taskTitle) {
        super(source);
        this.taskId = taskId;
        this.assigneeId = assigneeId;
        this.taskTitle = taskTitle;
    }
}
