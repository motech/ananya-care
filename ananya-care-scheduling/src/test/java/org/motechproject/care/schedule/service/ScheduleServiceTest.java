package org.motechproject.care.schedule.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.care.schedule.vaccinations.ChildVaccinationSchedule;
import org.motechproject.commons.date.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.commons.date.util.DateUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ScheduleServiceTest {

    @Mock
    private ScheduleTrackingService trackingService;
    private ScheduleService schedulerService;
    private final String scheduleName = ChildVaccinationSchedule.Measles.getName();

    @Before
    public void setUp() {
        schedulerService = new ScheduleService(trackingService);
    }

    @Test
    public void shouldEnrollChildIfNotEnrolledForMeaslesAlready() {
        DateTime dob = new DateTime(2011, 6, 10, 0, 0);
        String caseId = "caseId";
        DateTime now = DateTime.now();
        when(trackingService.search(any(EnrollmentsQuery.class))).thenReturn(new ArrayList<EnrollmentRecord>());

        schedulerService.enroll(caseId, dob, scheduleName);

        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(trackingService).enroll(captor.capture());
        EnrollmentRequest enrollmentRequest = captor.getValue();
        assertEquals(dob.toLocalDate(), enrollmentRequest.getReferenceDate());
        assertEquals(DateUtil.today(), enrollmentRequest.getEnrollmentDateTime().toLocalDate());
        assertNull(enrollmentRequest.getPreferredAlertTime());
        assertEquals(scheduleName, enrollmentRequest.getScheduleName());
    }

    @Test
    public void shouldNotEnrollChildIfEnrolledForMeaslesAlready() {
        DateTime dob = new DateTime(2011, 6, 10, 0, 0);
        String caseId = "caseId";
        when(trackingService.search(any(EnrollmentsQuery.class))).thenReturn(dummyEnrollmentRecordFromQuery(null));
        schedulerService.enroll(caseId, dob, ChildVaccinationSchedule.Measles.getName());
        verify(trackingService, never()).enroll(Matchers.<EnrollmentRequest>any());
    }

    @Test
    public void shouldFulfilMeaslesTakenIfScheduleIsCurrentlyNotFulfilledAndCurrentMilestone() {
        DateTime measlesTakenDateTime = new DateTime(2011, 6, 10, 10, 11);
        DateTime beforeTest = DateTime.now();
        String caseId = "caseId";
        when(trackingService.getEnrollment(caseId, scheduleName)).thenReturn(dummyEnrollmentRecord(MilestoneType.Measles.toString()));
        schedulerService.fulfillMilestone(caseId, MilestoneType.Measles.toString(), measlesTakenDateTime, scheduleName);

        ArgumentCaptor<Time> captor = ArgumentCaptor.forClass(Time.class);
        verify(trackingService).fulfillCurrentMilestone(eq(caseId), eq(scheduleName), eq(measlesTakenDateTime.toLocalDate()), captor.capture());

        Time fulfillmentTime = captor.getValue();
        assertFalse(fulfillmentTime.isBefore(DateUtil.time(beforeTest)));
        assertTrue(fulfillmentTime.isBefore(DateUtil.time(DateTime.now().plusMinutes(3))));
    }

    @Test
    public void shouldNotTryToFulfillIfScheduleNotEnrolledOrEnrollmentComplete() {
        String caseId = "caseId";
        when(trackingService.getEnrollment(caseId, scheduleName)).thenReturn(null);
        schedulerService.fulfillMilestone(caseId, MilestoneType.Measles.toString(), new DateTime(2011, 6, 10, 10, 11), scheduleName);
        verify(trackingService, never()).fulfillCurrentMilestone(any(String.class), any(String.class), any(LocalDate.class), any(Time.class));
    }

    @Test
    public void shouldEnrollMotherForMeaslesVaccinationWithPreferredAlertTimeSetToNull() {
        DateTime dob = new DateTime(2012, 10, 10, 0, 0);
        String caseId = "caseId";

        when(trackingService.search(any(EnrollmentsQuery.class))).thenReturn(new ArrayList<EnrollmentRecord>());
        schedulerService.enroll(caseId, dob, ChildVaccinationSchedule.Measles.getName());

        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(trackingService).enroll(captor.capture());

        EnrollmentRequest enrollmentRequest = captor.getValue();

        Time preferredAlertTime = enrollmentRequest.getPreferredAlertTime();
        assertNull(preferredAlertTime);
    }

    @Test
    public void shouldEnrollMotherForMeaslesVaccinationWithReferenceTimeSetToNowPlus2Minutes() {
        DateTime dob = new DateTime(2012, 10, 10, 0, 0);
        String caseId = "caseId";

        DateTime beforeTest = DateTime.now();
        when(trackingService.search(any(EnrollmentsQuery.class))).thenReturn(new ArrayList<EnrollmentRecord>());
        schedulerService.enroll(caseId, dob, ChildVaccinationSchedule.Measles.getName());

        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(trackingService).enroll(captor.capture());

        EnrollmentRequest enrollmentRequest = captor.getValue();
        Time referenceTime = enrollmentRequest.getReferenceTime();
        DateTime afterTest = DateTime.now();

        assertFalse(referenceTime.isBefore(new Time(beforeTest.getHourOfDay(), beforeTest.getMinuteOfHour())));
        assertTrue(referenceTime.isBefore(new Time(afterTest.getHourOfDay(), afterTest.getMinuteOfHour() + 3)));
    }

    @Test
    public void shouldUnenrollFromScheduleIfEnrolledAndShouldReturnTheLatestUnenrolledRecord() {
        String caseId = "caseId";
        EnrollmentRecord enrollmentRecord = enrollmentRecord("caseId", "scheduleName", "myMilestone");

        when(trackingService.getEnrollment(caseId,scheduleName)).thenReturn(enrollmentRecord);
        EnrollmentRecord record = schedulerService.unenroll(caseId, scheduleName);


        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(trackingService).unenroll(eq(caseId), captor.capture());
        List scheduleNames = captor.getValue();
        assertEquals(1, scheduleNames.size());
        assertEquals(scheduleName, scheduleNames.get(0));
        assertEquals(enrollmentRecord,record);

    }

    @Test
    public void shouldUnenrollFromScheduleIfEnrolledAndShouldReturnTheAnyEnrollmentRecord() {
        String caseId = "caseId";
        EnrollmentRecord oldDefaultedEnrollmentRecord = enrollmentRecord("caseId", "scheduleName", "myMilestone");

        when(trackingService.getEnrollment(caseId,scheduleName)).thenReturn(null);
        when(trackingService.search(Matchers.<EnrollmentsQuery>any())).thenReturn(Arrays.asList(oldDefaultedEnrollmentRecord));
        EnrollmentRecord record = schedulerService.unenroll(caseId, scheduleName);


        ArgumentCaptor<List> captor = ArgumentCaptor.forClass(List.class);
        verify(trackingService).unenroll(eq(caseId), captor.capture());
        List scheduleNames = captor.getValue();
        assertEquals(1, scheduleNames.size());
        assertEquals(scheduleName, scheduleNames.get(0));
        assertEquals(oldDefaultedEnrollmentRecord,record);

    }

    private EnrollmentRecord dummyEnrollmentRecord(String currentMilestoneName) {
        return enrollmentRecord(null, null, currentMilestoneName);
    }

    private ArrayList<EnrollmentRecord> dummyEnrollmentRecordFromQuery(String currentMilestoneName) {
        ArrayList<EnrollmentRecord> enrollmentRecords = new ArrayList<EnrollmentRecord>();
        enrollmentRecords.add(enrollmentRecord(null, null, currentMilestoneName));
        return enrollmentRecords;
    }

    private EnrollmentRecord enrollmentRecord(String externalId, String scheduleName, String currentMileStoneName) {
        EnrollmentRecord enrollmentRecord = new EnrollmentRecord();
        enrollmentRecord.setCurrentMilestoneName(currentMileStoneName);
        enrollmentRecord.setExternalId(externalId);
        enrollmentRecord.setScheduleName(scheduleName);
        return enrollmentRecord;
    }

    @Test
    public void shouldRegisterScheduleJsons() {
        verify(trackingService).add("test-1");
        verify(trackingService).add("test-2");
        verifyNoMoreInteractions(trackingService);
    }
}
