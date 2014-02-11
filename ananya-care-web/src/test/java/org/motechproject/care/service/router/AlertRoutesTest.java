package org.motechproject.care.service.router;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.schedule.vaccinations.ExpirySchedule;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.router.action.*;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import static org.joda.time.Period.weeks;
import static org.mockito.Matchers.any;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.commons.date.util.DateUtil.newDateTime;

public class AlertRoutesTest {
    @Mock
    private AlertChildAction alertChildAction;
    @Mock
    private AlertMotherAction alertMotherAction;
    @Mock
    private ClientExpiryAction clientExpiryAction;
    @Mock
    private Hep0ExpiryAction hep0ExpiryAction;
    @Mock
    private Opv0ExpiryAction opv0ExpiryAction;
    @Mock
    private BcgExpiryAction bcgExpiryAction;

    private AlertRoutes alertRoutes;

    @Before
    public void setUp(){
        initMocks(this);
        alertRoutes = new AlertRoutes(alertChildAction, alertMotherAction, clientExpiryAction, hep0ExpiryAction, opv0ExpiryAction, bcgExpiryAction);
    }

    @Test
    public void shouldInvokeVaccinationExpiryActionIfEventIsForExpiredVaccination(){
        verifyWasCalledFor(ChildVaccinationSchedule.Hepatitis0.getName(), hep0ExpiryAction, WindowName.late.name());
        verifyWasCalledFor(ChildVaccinationSchedule.OPV0.getName(), opv0ExpiryAction, WindowName.late.name());
        verifyWasCalledFor(ChildVaccinationSchedule.Bcg.getName(), bcgExpiryAction, WindowName.late.name());
    }

    @Test
    public void shouldInvokeAlertExpiryVaccinationIfEventIsForClientExpiry(){
        verifyWasCalledFor(ExpirySchedule.ChildCare.getName(), clientExpiryAction);
        verifyWasCalledFor(ExpirySchedule.MotherCare.getName(), clientExpiryAction);
    }

    @Test
    public void shouldInvokeAlertMotherVaccinationIfEventIsForMotherSchedule(){
        verifyWasCalledFor(MotherVaccinationSchedule.TT.getName(), alertMotherAction);
        verifyWasCalledFor(MotherVaccinationSchedule.Anc.getName(), alertMotherAction);
        verifyWasCalledFor(MotherVaccinationSchedule.Anc4.getName(), alertMotherAction);
        verifyWasCalledFor(MotherVaccinationSchedule.TTBooster.getName(), alertMotherAction);
    }

    @Test
    public void shouldInvokeAlertChildVaccinationIfEventIsForChildSchedule(){
        verifyWasCalledFor(ChildVaccinationSchedule.Measles.getName(), alertChildAction);
        verifyWasCalledFor(ChildVaccinationSchedule.Vita.getName(), alertChildAction);
        verifyWasCalledFor(ChildVaccinationSchedule.Bcg.getName(), alertChildAction);
        verifyWasCalledFor(ChildVaccinationSchedule.DPT.getName(), alertChildAction);
        verifyWasCalledFor(ChildVaccinationSchedule.Hepatitis.getName(), alertChildAction);
        verifyWasCalledFor(ChildVaccinationSchedule.Hepatitis0.getName(), alertChildAction);
        verifyWasCalledFor(ChildVaccinationSchedule.OPV.getName(), alertChildAction);
        verifyWasCalledFor(ChildVaccinationSchedule.OPVBooster.getName(), alertChildAction);
        verifyWasCalledFor(ChildVaccinationSchedule.OPV0.getName(), alertChildAction);
    }

    @Test(expected = NoRoutesMatchException.class)
    public void shouldThrowExceptionIfNoRouteFound() {
        Milestone milestone = new Milestone("milestonename", weeks(1), weeks(1), weeks(1), weeks(1));
        DateTime referenceDateTime = newDateTime(2000, 1, 1, 0, 0, 0);
        MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, referenceDateTime);

        MilestoneEvent milestoneEvent = new MilestoneEvent(null, "random", milestoneAlert, "random", null, null);
        alertRoutes.route(milestoneEvent);
    }

    private void verifyWasCalledFor(String scheduleName, Action alertClientAction) {
        verifyWasCalledFor(scheduleName, alertClientAction, null);
    }
    private void verifyWasCalledFor(String scheduleName, Action alertClientAction, String windowName) {
        Milestone milestone = new Milestone("milestonename", weeks(1), weeks(1), weeks(1), weeks(1));
        DateTime referenceDateTime = newDateTime(2000, 1, 1, 0, 0, 0);
        MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, referenceDateTime);

        MilestoneEvent milestoneEvent = new MilestoneEvent(null, scheduleName, milestoneAlert, windowName, null, null);
        alertRoutes.route(milestoneEvent);
        Mockito.verify(alertClientAction).invoke(any(MilestoneEvent.class));
        initMocks(this);
        alertRoutes = new AlertRoutes(alertChildAction, alertMotherAction, clientExpiryAction, hep0ExpiryAction, opv0ExpiryAction, bcgExpiryAction);
    }

}
