package org.motechproject.care.service.router.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.service.schedule.Opv0Service;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class Opv0ExpiryActionTest {
    @Mock
    AllChildren allChildren;
    @Mock
    Opv0Service opv0Service;
    Opv0ExpiryAction opv0ExpiryAction;

    @Before
    public void setUp(){
        initMocks(this);
        opv0ExpiryAction = new Opv0ExpiryAction(opv0Service,allChildren);
    }

    @Test
    public void shouldUnEnrollChildFromOpv0ExpiredSchedule() {
        String caseId = "caseID";
        Child child = new Child();
        child.setCaseId(caseId);
        MilestoneEvent milestoneEvent = new MilestoneEvent(caseId, null, null, WindowName.late.name(), null, null);
        when(allChildren.findByCaseId(caseId)).thenReturn(child);
        opv0ExpiryAction.invoke(milestoneEvent);
        verify(opv0Service).close(child);
    }
}
