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
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeaslesSchedulerServiceTest {

    @Mock
    private ScheduleTrackingService trackingService;
    private MeaslesSchedulerService measlesSchedulerService;

    @Before
    public void setUp(){
        measlesSchedulerService = new MeaslesSchedulerService(trackingService);
    }

    @Test
    public void shouldEnrollChildIfNotEnrolledFOrMeaslesAlready(){
        DateTime dob = new DateTime(2011, 6, 10, 0, 0);
        String caseId = "caseId";
        when(trackingService.getEnrollment(caseId, measlesSchedulerService.getScheduleName())).thenReturn(null);
        measlesSchedulerService.enroll(caseId, dob);
        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(trackingService).enroll(captor.capture());
        EnrollmentRequest enrollmentRequest = captor.getValue();
        assertEquals(dob.toLocalDate(), enrollmentRequest.getReferenceDate());
        assertEquals(DateUtil.today(), enrollmentRequest.getEnrollmentDateTime().toLocalDate());
        assertEquals(measlesSchedulerService.getScheduleName(), enrollmentRequest.getScheduleName());
    }

    @Test
    public void shouldNotEnrollChildIfEnrolledFOrMeaslesAlready(){
        DateTime dob = new DateTime(2011, 6, 10, 0, 0);
        String caseId = "caseId";
        when(trackingService.getEnrollment(caseId, measlesSchedulerService.getScheduleName())).thenReturn(dummyEnrollmentRecord(null));
        measlesSchedulerService.enroll(caseId, dob);
        verify(trackingService, never()).enroll(Matchers.<EnrollmentRequest>any());
    }

    @Test
    public void shouldFulfilMeaslesTakenIfScheduleIsCurrentlyNotFulfilledAndCurrentMilestone(){
        DateTime measlesTakenDateTime = new DateTime(2011, 6, 10, 10, 11);
        String caseId = "caseId";
        when(trackingService.getEnrollment(caseId, measlesSchedulerService.getScheduleName())).thenReturn(dummyEnrollmentRecord(MeaslesSchedulerService.milestone));
        measlesSchedulerService.fulfilMeaslesTaken(caseId, measlesTakenDateTime);
        verify(trackingService).fulfillCurrentMilestone(eq(caseId), eq(measlesSchedulerService.getScheduleName()), eq(measlesTakenDateTime.toLocalDate()), eq(DateUtil.time(measlesTakenDateTime)));
    }

    @Test
    public void shouldNotTryToFulfillIfScheduleNotEnrolledOrEnrollmentComplete(){
        String caseId = "caseId";
        when(trackingService.getEnrollment(caseId, measlesSchedulerService.getScheduleName())).thenReturn(null);
        measlesSchedulerService.fulfilMeaslesTaken(caseId, new DateTime(2011, 6, 10, 10, 11));
        verify(trackingService, never()).fulfillCurrentMilestone(any(String.class), any(String.class), any(LocalDate.class), any(Time.class));
    }


    private EnrollmentRecord dummyEnrollmentRecord(String currentMilestoneName) {
        return new EnrollmentRecord(null, null, currentMilestoneName, null, null, null, null, null, null, null);
    }
}
