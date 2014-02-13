package org.motechproject.care.service.router.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.care.schedule.vaccinations.ExpirySchedule;
import org.motechproject.care.service.ChildService;
import org.motechproject.care.service.MotherService;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.MockitoAnnotations.initMocks;

public class ClientExpiryActionTest {
    @Mock
    private MotherService motherService;
    @Mock
    private ChildService childService;
    ClientExpiryAction clientExpiryAction;

    @Before
    public void setUp(){
       initMocks(this);
       clientExpiryAction = new ClientExpiryAction(motherService, childService);
    }

    @Test
    public void shouldExpireMotherSchedulesWhenMotherExpired(){
        String caseId = "";
        String scheduleName = ExpirySchedule.MotherCare.getName();
        MilestoneEvent milestoneEvent = new MilestoneEvent(caseId, scheduleName, null, null, null, null);

        when(motherService.expireCase(caseId)).thenReturn(true);
        clientExpiryAction.invoke(milestoneEvent);
        verify(motherService).expireCase(caseId);
        verify(childService, never()).expireCase(caseId);
    }

    @Test
    public void shouldExpireChildSchedulesWhenChildExpired(){
        String caseId = "";
        String scheduleName = ExpirySchedule.ChildCare.getName();
        MilestoneEvent milestoneEvent = new MilestoneEvent(caseId, scheduleName, null, null, null, null);

        when(motherService.expireCase(caseId)).thenReturn(false);
        clientExpiryAction.invoke(milestoneEvent);
        verify(childService).expireCase(caseId);
    }
}
