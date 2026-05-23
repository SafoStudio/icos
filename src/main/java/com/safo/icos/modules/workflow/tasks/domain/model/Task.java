package com.safo.icos.modules.workflow.tasks.domain.model;

import com.safo.icos.modules.workflow.tasks.domain.model.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;
import java.util.Map;
import java.util.UUID;

@Getter
@Builder
public class Task {
    private final UUID id;
    private final String title;
    private final String description;
    private final TaskStatus status;
    private final UUID assigneeId;
    private final UUID departmentId;
    // Flexible metadata for enterprise use cases
    private final Map<String, String> metadata;

    public void moveTo(TaskStatus newStatus) {
        // Logic for state transition validation can be added here
    }
}
