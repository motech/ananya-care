package org.motechproject.care.schedule.service.listener;

import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.server.event.annotations.MotechListener;

/**
 * Created by IntelliJ IDEA.
 * User: pchandra
 * Date: 4/5/12
 * Time: 11:27 AM
 * To change this template use File | Settings | File Templates.
 */
public class CareAlertListener {

    @MotechListener(subjects = {EventSubjects.MILESTONE_ALERT})
    public void handle(MotechEvent event){
        MilestoneEvent msEvent = new MilestoneEvent(event);
        String externalId = msEvent.getExternalId();
        MilestoneAlert milestoneAlert = msEvent.getMilestoneAlert();
        
        System.out.println(milestoneAlert.getEarliestDateTime());
        System.out.println(milestoneAlert.getLateDateTime());

    }
}