package org.motechproject.care.schedule.vaccinations;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MeaslesTest  {

    @Mock
    private ScheduleTrackingService trackingService;
    private Measles measles;

    @Before
    public void setUp(){
        measles = new Measles(trackingService);
    }

    @Test
    public void shouldEnrollChildIfNotEnrolledFOrMeaslesAlready(){
        DateTime dob = new DateTime(2011, 6, 10, 0, 0);
        String caseId = "caseId";
        when(trackingService.getEnrollment(caseId,measles.getScheduleName())).thenReturn(null);
        measles.process(caseId, dob);
        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(trackingService).enroll(captor.capture());
        EnrollmentRequest enrollmentRequest = captor.getValue();
        assertEquals(dob.toLocalDate(), enrollmentRequest.getReferenceDate());
        assertEquals(DateUtil.today(), enrollmentRequest.getEnrollmentDateTime().toLocalDate());
        assertEquals(measles.getScheduleName(), enrollmentRequest.getScheduleName());
    }

    @Test
    public void shouldNotEnrollChildIfEnrolledFOrMeaslesAlready(){
        DateTime dob = new DateTime(2011, 6, 10, 0, 0);
        String caseId = "caseId";
        when(trackingService.getEnrollment(caseId, measles.getScheduleName())).thenReturn(new EnrollmentRecord(null,null,null,null,null,null,null,null,null,null));
        measles.process(caseId, dob);
        verify(trackingService, never()).enroll(Matchers.<EnrollmentRequest>any());
    }
}
