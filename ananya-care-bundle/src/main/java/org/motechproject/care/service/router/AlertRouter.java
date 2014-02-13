package org.motechproject.care.service.router;

import org.apache.log4j.Logger;
import org.motechproject.event.MotechEvent;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.scheduletracking.api.events.constants.EventSubjects;
import org.motechproject.event.listener.annotations.MotechListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component
public class AlertRouter {
    public static final String IGNORE_VACCINATION_ALERTS_PROPERTY = "ignore.vaccination.alerts";
    Logger logger = Logger.getLogger(AlertRouter.class);

    private AlertRoutes alertRoutes;
    private boolean ignoreVaccinationAlerts;

    @Autowired
    public AlertRouter(AlertRoutes alertRoutes, @Qualifier("ananyaCareProperties") Properties ananyaCareProperties) {
        this.alertRoutes = alertRoutes;
        this.ignoreVaccinationAlerts = Boolean.parseBoolean(ananyaCareProperties.getProperty(IGNORE_VACCINATION_ALERTS_PROPERTY));
    }

    @MotechListener(subjects = {EventSubjects.MILESTONE_ALERT})
    public void handle(MotechEvent realEvent) {
        MilestoneEvent event = new MilestoneEvent(realEvent);
        MilestoneAlert milestoneAlert = event.getMilestoneAlert();
        logger.info( String.format("Received alert -- ScheduleName: %s, MilestoneName: %s, WindowName: %s, ExternalId: %s", event.getScheduleName(), milestoneAlert.getMilestoneName(), event.getWindowName(), event.getExternalId()));

        if(shouldIgnoreVaccinationAlerts()) {
            logger.info(String.format("%s is set to true. Ignoring alert.", AlertRouter.IGNORE_VACCINATION_ALERTS_PROPERTY));
            return;
        }

        alertRoutes.route(event);
    }

    private boolean shouldIgnoreVaccinationAlerts() {
        return ignoreVaccinationAlerts;
    }
}
