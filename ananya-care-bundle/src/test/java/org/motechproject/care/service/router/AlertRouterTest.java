package org.motechproject.care.service.router;


import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static org.joda.time.Period.weeks;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;

public class AlertRouterTest {

    private AlertRouter alertRouter;

    @Mock
    private AlertRoutes alertRoutes;


    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldRouteAlertsToAlertRouter() {
        alertRouter = new AlertRouter(alertRoutes, new Properties());

        Milestone milestone = new Milestone("milestonename", weeks(1), weeks(1), weeks(1), weeks(1));
        DateTime referenceDateTime = newDateTime(2000, 1, 1, 0, 0, 0);
        MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, referenceDateTime);

        MilestoneEvent milestoneEvent = new MilestoneEvent("myExternalId", "myScheduleName", milestoneAlert, "myWindow", new DateTime(), null);

        alertRouter.handle(milestoneEvent.toMotechEvent());

        ArgumentCaptor<MilestoneEvent> captor = ArgumentCaptor.forClass(MilestoneEvent.class);
        verify(alertRoutes).route(captor.capture());
        MilestoneEvent actualMilestoneEvent = captor.getValue();
        assertEquals(actualMilestoneEvent.getExternalId(), milestoneEvent.getExternalId());
        assertEquals(actualMilestoneEvent.getScheduleName(), milestoneEvent.getScheduleName());
        assertEquals(actualMilestoneEvent.getWindowName(), milestoneEvent.getWindowName());
        assertEquals(actualMilestoneEvent.getMilestoneAlert(), milestoneEvent.getMilestoneAlert());
        assertEquals(actualMilestoneEvent.getReferenceDateTime(), milestoneEvent.getReferenceDateTime());
    }

    @Test
    public void shouldNotRouteAlertsIfIgnoreVaccinationAlertsIsSetToTrue() {
        Properties ananyaCareProperties = new Properties();
        ananyaCareProperties.setProperty(AlertRouter.IGNORE_VACCINATION_ALERTS_PROPERTY, "true");
        alertRouter = new AlertRouter(alertRoutes, ananyaCareProperties);

        Milestone milestone = new Milestone("milestonename", weeks(1), weeks(1), weeks(1), weeks(1));
        DateTime referenceDateTime = newDateTime(2000, 1, 1, 0, 0, 0);
        MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, referenceDateTime);

        MilestoneEvent milestoneEvent = new MilestoneEvent("myExternalId", "myScheduleName", milestoneAlert, "myWindow", new DateTime(), null);

        alertRouter.handle(milestoneEvent.toMotechEvent());
        verify(alertRoutes, never()).route(any(MilestoneEvent.class));
    }
}
