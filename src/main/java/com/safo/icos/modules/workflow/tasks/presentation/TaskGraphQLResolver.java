package com.safo.icos.modules.workflow.tasks.presentation;

import com.safo.icos.modules.workflow.tasks.application.TaskService;
import com.safo.icos.modules.workflow.tasks.domain.model.Task;
import com.safo.icos.modules.workflow.tasks.domain.model.enums.TaskStatus;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class TaskGraphQLResolver {

    private final TaskService taskService;

    @MutationMapping
    public Task createTask(@Argument String title, @Argument String description, @Argument UUID assigneeId, @Argument UUID departmentId) {
        return taskService.createTask(title, description, assigneeId, departmentId);
    }

    @MutationMapping
    public Task moveTask(@Argument UUID taskId, @Argument String newStatus) {
        return taskService.moveTask(taskId, TaskStatus.valueOf(newStatus));
    }
}
