package com.safo.icos.modules.workflow.tasks.api.event;

import com.safo.icos.modules.workflow.tasks.domain.model.enums.TaskStatus;
import java.util.UUID;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class TaskStatusChangedEvent extends ApplicationEvent {
    private final UUID taskId;
    private final UUID assigneeId;
    private final String title;
    private final TaskStatus newStatus;

    public TaskStatusChangedEvent(Object source, UUID taskId, UUID assigneeId, String title, TaskStatus newStatus) {
        super(source);
        this.taskId = taskId;
        this.assigneeId = assigneeId;
        this.title = title;
        this.newStatus = newStatus;
    }
}
