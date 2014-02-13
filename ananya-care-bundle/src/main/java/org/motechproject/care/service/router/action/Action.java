package org.motechproject.care.service.router.action;

import org.motechproject.scheduletracking.api.events.MilestoneEvent;

public interface Action {
    void invoke(MilestoneEvent event);
}
