package org.motechproject.care.schedule.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.springframework.stereotype.Component;

@Component
public class CareAlertListener {

    public void handle(MotechEvent event){
        MilestoneEvent msEvent = new MilestoneEvent(event);
        String externalId = msEvent.getExternalId();
        MilestoneAlert milestoneAlert = msEvent.getMilestoneAlert();
        
        System.out.println(milestoneAlert.getEarliestDateTime());
        System.out.println(milestoneAlert.getLateDateTime());

    }
}
