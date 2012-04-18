package org.motechproject.care.service.schedule.listener;

import org.apache.log4j.Logger;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.server.event.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CareAlertListener {

    Logger logger = Logger.getLogger(CareAlertListener.class);
    private AlertChildVaccination alertChildVaccination;
    private AlertMotherVaccination alertMotherVaccination;

    @Autowired
    public CareAlertListener(AlertChildVaccination alertChildVaccination, AlertMotherVaccination alertMotherVaccination) {
        this.alertChildVaccination = alertChildVaccination;
        this.alertMotherVaccination = alertMotherVaccination;
    }

    @MotechListener(subjects = {EventSubjects.MILESTONE_ALERT})
    public void handleEvent(MotechEvent event){
        String scheduleName = new MilestoneEvent(event).getScheduleName();
        if(isChildSchedule(scheduleName))
            alertChildVaccination.invoke(event);
        else if(isMotherSchedule(scheduleName))
            alertMotherVaccination.invoke(event);
    }

    private boolean isChildSchedule(String scheduleName) {
        return ChildVaccinationSchedule.fromString(scheduleName) != null;
    }
    private boolean isMotherSchedule(String scheduleName) {
        return MotherVaccinationSchedule.fromString(scheduleName) != null;
    }

}
