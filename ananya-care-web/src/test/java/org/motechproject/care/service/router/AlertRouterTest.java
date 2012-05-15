package org.motechproject.care.service.router;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.schedule.vaccinations.ExpirySchedule;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.care.service.router.action.*;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import static org.mockito.Matchers.any;
import static org.mockito.MockitoAnnotations.initMocks;

public class AlertRouterTest {
    @Mock
    private MilestoneAlert milestoneAlert;
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

    AlertRouter alertRouter;

    @Before
    public void setUp(){
        initMocks(this);
        alertRouter = new AlertRouter(alertChildAction, alertMotherAction, clientExpiryAction, hep0ExpiryAction, opv0ExpiryAction, bcgExpiryAction);
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

    private void verifyWasCalledFor(String scheduleName, Action alertClientAction) {
        verifyWasCalledFor(scheduleName, alertClientAction, null);
    }
    private void verifyWasCalledFor(String scheduleName, Action alertClientAction, String windowName) {
        MilestoneEvent milestoneEvent = new MilestoneEvent(null, scheduleName, milestoneAlert, windowName, null);
        alertRouter.handle(milestoneEvent.toMotechEvent());
        Mockito.verify(alertClientAction).invoke(any(MilestoneEvent.class));
        initMocks(this);
        alertRouter = new AlertRouter(alertChildAction, alertMotherAction, clientExpiryAction, hep0ExpiryAction, opv0ExpiryAction, bcgExpiryAction);
    }

}