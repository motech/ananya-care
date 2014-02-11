package org.motechproject.care.service.router.action;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.care.domain.Child;
import org.motechproject.care.repository.AllChildren;
import org.motechproject.care.service.schedule.BcgService;
import org.motechproject.scheduletracking.api.domain.WindowName;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class BcgExpiryActionTest {
    @Mock
    AllChildren allChildren;
    @Mock
    BcgService bcgService;
    BcgExpiryAction bcgExpiryAction;

    @Before
    public void setUp(){
        initMocks(this);
        bcgExpiryAction = new BcgExpiryAction(bcgService,allChildren);
    }

    @Test
    public void shouldUnEnrollChildFromBcgExpiredSchedule() {
        String caseId = "caseID";
        Child child = new Child();
        child.setCaseId(caseId);
        MilestoneEvent milestoneEvent = new MilestoneEvent(caseId, null, null, WindowName.late.name(), null, null);
        when(allChildren.findByCaseId(caseId)).thenReturn(child);
        bcgExpiryAction.invoke(milestoneEvent);
        verify(bcgService).close(child);
    }

}
