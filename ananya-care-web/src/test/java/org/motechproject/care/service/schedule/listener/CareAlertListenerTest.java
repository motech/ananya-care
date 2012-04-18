package org.motechproject.care.service.schedule.listener;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.care.schedule.vaccinations.MotherVaccinationSchedule;
import org.motechproject.model.MotechEvent;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class CareAlertListenerTest {
    @Mock
    private AlertChildVaccination alertChildVaccination;
    @Mock
    private AlertMotherVaccination alertMotherVaccination;
    CareAlertListener careAlertListener;

    @Before
    public void setUp(){
        initMocks(this);
        careAlertListener = new CareAlertListener(alertChildVaccination, alertMotherVaccination);
    }

    @Test
    public void shouldInvokeAlertMotherVaccinationIfEventIsForMotherSchedule(){
        verifyWasCalledFor(MotherVaccinationSchedule.TT.getName(), alertMotherVaccination);
    }

    @Test
    public void shouldInvokeAlertChildVaccinationIfEventIsForChildSchedule(){
        verifyWasCalledFor(ChildVaccinationSchedule.Measles.getName(), alertChildVaccination);
        verifyWasCalledFor(ChildVaccinationSchedule.Vita.getName(), alertChildVaccination);
        verifyWasCalledFor(ChildVaccinationSchedule.Bcg.getName(), alertChildVaccination);
    }

    private void verifyWasCalledFor(String scheduleName, AlertVaccination alertVaccination) {
        MotechEvent event = new MilestoneEvent(null, scheduleName, null, null, null).toMotechEvent();
        careAlertListener.handleEvent(event);
        Mockito.verify(alertVaccination).invoke(event);
    }
}
