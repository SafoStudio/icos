package com.safo.icos.modules.timeoff.api.event;

import com.safo.icos.modules.timeoff.domain.model.LeaveRequest;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LeaveRequestRejectedEvent extends ApplicationEvent {
    private final LeaveRequest leaveRequest;

    public LeaveRequestRejectedEvent(Object source, LeaveRequest leaveRequest) {
        super(source);
        this.leaveRequest = leaveRequest;
    }
}
