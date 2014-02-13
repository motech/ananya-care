package org.motechproject.care.service.router.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.service.schedule.Hep0Service;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class Hep0ExpiryActionTest {
    @Mock
    AllChildren allChildren;
    @Mock
    Hep0Service hep0Service;
    Hep0ExpiryAction hep0ExpiryAction;

    @Before
    public void setUp(){
       initMocks(this);
       hep0ExpiryAction = new Hep0ExpiryAction(hep0Service,allChildren);
    }

    @Test
    public void shouldUnEnrollChildFromHep0ExpiredSchedule() {
        String caseId = "caseID";
        Child child = new Child();
        child.setCaseId(caseId);
        MilestoneEvent milestoneEvent = new MilestoneEvent(caseId, null, null, WindowName.late.name(), null, null);
        when(allChildren.findByCaseId(caseId)).thenReturn(child);
        hep0ExpiryAction.invoke(milestoneEvent);
        verify(hep0Service).close(child);
    }
}
