package org.motechproject.care.service.schedule.listener;

import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.commcare.gateway.CommcareCaseGateway;
import org.motechproject.commcare.request.CaseTask;
import org.motechproject.commcare.request.Pregnancy;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class CareAlertServiceListenerTest {

    @Mock
    private CommcareCaseGateway commcareCaseGateway;
    @Mock
    private AllMothers allMothers;

    Mother client;
    String caseId = "0A8MF30IJWI0FJW3JFW0J0W3A8";
    String taskId = "3F2504E04F8911D39A0C0305E82C3301";
    String flwId = "FLW1234";
    String motherName = "Sita";
    String groupId = "GRP1234";
    private String caseName = "TT 1";

    @Before
    public void setUp() {
        client = new Mother(caseId, "pregnancy",null, flwId, motherName, groupId, null, null, null, null, false, null, null, null, null, null, true);
        initMocks(this);
        when(allMothers.findByCaseId(caseId)).thenReturn(client);
    }

    @Test
    public void shouldSendRightCaseTaskObjectToGateway() {
        String scheduleName = "TT Vaccination";
        Milestone milestone = new Milestone(caseName, new Period(0, PeriodType.weeks()), new Period(0, PeriodType.weeks()), new Period(36, PeriodType.weeks()), null);
        MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, DateUtil.now());
        CaseTask caseTask;
        caseTask = createCaseTask(milestoneAlert, client);

        MilestoneEvent milestoneEvent = new MilestoneEvent(caseId, scheduleName, milestoneAlert, "due", DateUtil.now());
        CareAlertServiceListener careAlertServiceListener = new CareAlertServiceListener(commcareCaseGateway, allMothers);

        careAlertServiceListener.handleEvent(milestoneEvent.toMotechEvent());
        ArgumentCaptor<CaseTask> argumentCaptor = ArgumentCaptor.forClass(CaseTask.class);

        verify(commcareCaseGateway).submitCase(argumentCaptor.capture());

        CaseTask task = argumentCaptor.getValue();

        assertNotNull(task.getTaskId());
        assertNotNull(task.getDateModified());
        assertEquals(caseName, task.getCaseName());
        assertEquals("task", task.getCaseType());
        assertEquals(milestoneAlert.getDueDateTime().toString("yyyy-MM-dd"), task.getDateEligible());
        assertEquals(milestoneAlert.getLateDateTime().toString("yyyy-MM-dd"), task.getDateExpires());
        assertEquals("tt1", task.getTaskId());

        Pregnancy pregnancy = task.getPregnancy();
        assertEquals("pregnancy",pregnancy.getCase_type());
        assertEquals(caseId,pregnancy.getPregnancy_id());

    }


    private CaseTask createCaseTask(MilestoneAlert milestoneAlert, Mother client) {
        CaseTask caseTask = new CaseTask();
        caseTask.setCaseId(caseId);
        caseTask.setCaseName(milestoneAlert.getMilestoneName());
        caseTask.setDateEligible(milestoneAlert.getDueDateTime().toString());
        caseTask.setDateExpires(milestoneAlert.getLateDateTime().toString());
        caseTask.setOwnerId(client.getGroupId());
        return caseTask;
    }

}
