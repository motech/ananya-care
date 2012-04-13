package org.motechproject.care.service.schedule.listener;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.care.domain.CareCaseTask;
import org.motechproject.care.domain.Mother;
import org.motechproject.care.repository.AllCareCaseTasks;
import org.motechproject.care.repository.AllMothers;
import org.motechproject.care.request.CaseType;
import org.motechproject.casexml.domain.CaseTask;
import org.motechproject.casexml.gateway.CommcareCaseGateway;
import org.motechproject.scheduletracking.api.domain.Milestone;
import org.motechproject.scheduletracking.api.domain.MilestoneAlert;
import org.motechproject.scheduletracking.api.events.MilestoneEvent;
import org.motechproject.util.DateUtil;

import java.util.Properties;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.powermock.api.mockito.PowerMockito.when;

public class CareAlertServiceListenerTest {

    @Mock
    private CommcareCaseGateway commcareCaseGateway;
    @Mock
    private AllMothers allMothers;
    @Mock
    private AllCareCaseTasks allCareCaseTasks;
    @Mock
    private Properties ananyaCareProperties;

    private CareAlertServiceListener careAlertServiceListener;


    @Before
    public void setUp() {
        initMocks(this);
        this.careAlertServiceListener = new CareAlertServiceListener(commcareCaseGateway, allMothers, allCareCaseTasks, ananyaCareProperties);
    }

    @Test
    public void shouldSendRightCaseTaskObjectToGateway() {
        String scheduleName = "TT Vaccination";
        String motherCaseId = "0A8MF30IJWI0FJW3JFW0J0W3A8";
        String caseName = "TT 1";
        String groupId = "groupId";
        String flwId = "FLW1234";
        String motherName = "Sita";
        DateTime startOfSchedule = DateUtil.now();

        Milestone milestone = new Milestone(caseName, weeks(0), weeks(36), null, null);
        MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, startOfSchedule);
        MilestoneEvent milestoneEvent = new MilestoneEvent(motherCaseId, scheduleName, milestoneAlert, "due", startOfSchedule);

        Mother client = new Mother(motherCaseId, null, flwId, motherName, groupId, DateTime.now().plusYears(1), null, null, null, false, null, null, null, null, null, true);
        when(allMothers.findByCaseId(motherCaseId)).thenReturn(client);
        String commCareUrl = "commCareUrl";
        String motechUserId = "motechUserId";
        when(ananyaCareProperties.getProperty("commcare.hq.url")).thenReturn(commCareUrl);
        when(ananyaCareProperties.getProperty("motech.user.id")).thenReturn(motechUserId);
        careAlertServiceListener.handleEvent(milestoneEvent.toMotechEvent());


        ArgumentCaptor<CaseTask> argumentCaptor = ArgumentCaptor.forClass(CaseTask.class);
        verify(commcareCaseGateway).submitCase(eq(commCareUrl),argumentCaptor.capture());
        CaseTask task = argumentCaptor.getValue();

        assertNotNull(task.getTaskId());
        assertNotNull(task.getCurrentTime());
        assertEquals(caseName, task.getCaseName());
        assertEquals(startOfSchedule.toString("yyyy-MM-dd"), task.getDateEligible());
        assertEquals(startOfSchedule.plusWeeks(36).toString("yyyy-MM-dd"), task.getDateExpires());
        assertEquals(groupId, task.getOwnerId());
        assertEquals("tt_1", task.getTaskId());
        assertEquals(motherCaseId,task.getClientCaseId());
        assertEquals(CaseType.Mother.getType(),task.getClientCaseType());
        assertEquals(motechUserId,task.getMotechUserId());
    }

    public static Period weeks(int numberOfWeeks) {
        return new Period(0, 0, numberOfWeeks, 0, 0, 0, 0, 0);
    }


    @Test
    public void shouldHandleWhenExpiryDateIsBeyondEDDBeforeSendingToGateway() {
        String scheduleName = "TT Vaccination";
        String motherCaseId = "0A8MF30IJWI0FJW3JFW0J0W3A8";
        String caseName = "TT 1";
        String groupId = "groupId";
        String flwId = "FLW1234";
        String motherName = "Sita";
        DateTime edd = DateTime.parse("2012-10-01");
        DateTime startScheduleDate = DateTime.parse("2012-08-01");

        Milestone milestone = new Milestone(caseName, null, weeks(36), null, null);
        MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, startScheduleDate);
        MilestoneEvent milestoneEvent = new MilestoneEvent(motherCaseId, scheduleName, milestoneAlert, "due", DateUtil.now());

        Mother client = new Mother(motherCaseId, null, flwId, motherName, groupId, edd, null, null, null, false, null, null, null, null, null, true);
        when(allMothers.findByCaseId(motherCaseId)).thenReturn(client);
        String commCareUrl = "commCareUrl";
        String motechUserId = "motechUserId";
        when(ananyaCareProperties.getProperty("commcare.hq.url")).thenReturn(commCareUrl);
        when(ananyaCareProperties.getProperty("motech.user.id")).thenReturn(motechUserId);
        careAlertServiceListener.handleEvent(milestoneEvent.toMotechEvent());


        ArgumentCaptor<CaseTask> argumentCaptor = ArgumentCaptor.forClass(CaseTask.class);
        verify(commcareCaseGateway).submitCase(eq(commCareUrl), argumentCaptor.capture());
        CaseTask task = argumentCaptor.getValue();

        assertNotNull(task.getTaskId());
        assertNotNull(task.getCurrentTime());
        assertEquals(caseName, task.getCaseName());
        assertEquals(startScheduleDate.toString("yyyy-MM-dd"), task.getDateEligible());
        assertEquals(edd.toString("yyyy-MM-dd"), task.getDateExpires());
        assertEquals(groupId, task.getOwnerId());
        assertEquals("tt_1", task.getTaskId());
        assertEquals(motherCaseId,task.getClientCaseId());
        assertEquals(CaseType.Mother.getType(),task.getClientCaseType());
        assertEquals(motechUserId,task.getMotechUserId());
    }

    @Test
    public void shouldNotSendAlertIfDueDateStartFallsAfterEDD() {
        String scheduleName = "TT Vaccination";
        String motherCaseId = "0A8MF30IJWI0FJW3JFW0J0W3A8";
        String caseName = "TT 1";
        String groupId = "groupId";
        String flwId = "FLW1234";
        String motherName = "Sita";
        DateTime edd = DateTime.parse("2012-07-01");
        DateTime startScheduleDate = DateTime.parse("2012-08-01");

        Milestone milestone = new Milestone(caseName, null, weeks(36), null, null);
        MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, startScheduleDate);
        MilestoneEvent milestoneEvent = new MilestoneEvent(motherCaseId, scheduleName, milestoneAlert, "due", DateUtil.now());

        Mother client = new Mother(motherCaseId, null, flwId, motherName, groupId, edd, null, null, null, false, null, null, null, null, null, true);
        when(allMothers.findByCaseId(motherCaseId)).thenReturn(client);
        careAlertServiceListener.handleEvent(milestoneEvent.toMotechEvent());

        verify(commcareCaseGateway, never()).submitCase(anyString(), any(CaseTask.class));
    }

    @Test
    public void shouldSaveTaskToDb() {
        String scheduleName = "TT Vaccination";
        String motherCaseId = "0A8MF30IJWI0FJW3JFW0J0W3A8";
        String caseName = "TT 1";
        String groupId = "groupId";
        String flwId = "FLW1234";
        String motherName = "Sita";
        DateTime startScheduleDate = DateUtil.now();

        Milestone milestone = new Milestone(caseName, weeks(0), weeks(36), null, null);
        MilestoneAlert milestoneAlert = MilestoneAlert.fromMilestone(milestone, startScheduleDate);
        MilestoneEvent milestoneEvent = new MilestoneEvent(motherCaseId, scheduleName, milestoneAlert, "due", startScheduleDate);

        Mother client = new Mother(motherCaseId, null, flwId, motherName, groupId, startScheduleDate.plusYears(1), null, null, null, false, null, null, null, null, null, true);
        when(allMothers.findByCaseId(motherCaseId)).thenReturn(client);

        String motechUserId = "motechUserId";
        when(ananyaCareProperties.getProperty("motech.user.id")).thenReturn(motechUserId);
        careAlertServiceListener.handleEvent(milestoneEvent.toMotechEvent());

        ArgumentCaptor<CareCaseTask> careCaseTaskArgumentCaptor = ArgumentCaptor.forClass(CareCaseTask.class);
        verify(allCareCaseTasks).add(careCaseTaskArgumentCaptor.capture());

        CareCaseTask task = careCaseTaskArgumentCaptor.getValue();

        assertNotNull(task.getTaskId());
        assertNotNull(task.getCurrentTime());
        assertEquals(caseName, task.getCaseName());
        assertEquals("task", task.getCaseType());
        assertEquals(startScheduleDate.toString("yyyy-MM-dd"), task.getDateEligible());
        assertEquals(startScheduleDate.plusWeeks(36).toString("yyyy-MM-dd"), task.getDateExpires());
        assertEquals(groupId, task.getOwnerId());
        assertEquals("tt_1", task.getTaskId());
        assertEquals(motherCaseId,task.getClientCaseId());
        assertEquals(CaseType.Mother.getType(),task.getClientCaseType());
        assertEquals(motechUserId,task.getMotechUserId());
    }

}
