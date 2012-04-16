package org.motechproject.care.schedule.service;

import junit.framework.Assert;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(MockitoJUnitRunner.class)
public class CareScheduleTrackingServiceTest{

    @Mock
    private ScheduleTrackingService scheduleTrackingService;
    CareScheduleTrackingService careScheduleTrackingService;

    @Before
    public void before(){
        initMocks(this);
        careScheduleTrackingService = new CareScheduleTrackingService(scheduleTrackingService);
    }

    @Test
    public void shouldNotEnrollMotherWhenEddNull(){
        careScheduleTrackingService.enroll("motherCaseId", null);
        verify(scheduleTrackingService, never()).enroll(any(EnrollmentRequest.class));
    }

    @Test
    public void shouldEnrollMotherForTTVaccination(){
        DateTime edd = new DateTime(2012, 10, 10, 0, 0);
        String motherCaseId = "motherCaseId";

        when(scheduleTrackingService.getEnrollment("motherCaseId", CareScheduleTrackingService.ttVaccinationScheduleName)).thenReturn(null);
        careScheduleTrackingService.enroll(motherCaseId, edd);

        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(scheduleTrackingService).enroll(captor.capture());
        verify(scheduleTrackingService).getEnrollment("motherCaseId", CareScheduleTrackingService.ttVaccinationScheduleName);

        EnrollmentRequest enrollmentRequest = captor.getValue();
        Assert.assertEquals(DateUtil.today(), enrollmentRequest.getReferenceDate());
        Assert.assertNotNull(enrollmentRequest.getPreferredAlertTime());
        Assert.assertNotNull(enrollmentRequest.getEnrollmentDateTime());
        Assert.assertEquals(motherCaseId, enrollmentRequest.getExternalId());
        Assert.assertEquals(CareScheduleTrackingService.ttVaccinationScheduleName, enrollmentRequest.getScheduleName());
        Assert.assertNotNull(enrollmentRequest.getEnrollmentDateTime());
    }

    @Test
    public void shouldEnrollMotherForTTVaccinationWithPreferredAlertTimeSetFewMinutesInAdvance(){
        DateTime edd = new DateTime(2012, 10, 10, 0, 0);
        String motherCaseId = "motherCaseId";

        when(scheduleTrackingService.getEnrollment("motherCaseId", CareScheduleTrackingService.ttVaccinationScheduleName)).thenReturn(null);
        careScheduleTrackingService.enroll(motherCaseId, edd);

        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(scheduleTrackingService).enroll(captor.capture());
        verify(scheduleTrackingService).getEnrollment("motherCaseId", CareScheduleTrackingService.ttVaccinationScheduleName);

        EnrollmentRequest enrollmentRequest = captor.getValue();

        Time preferredAlertTime = enrollmentRequest.getPreferredAlertTime();
        DateTime preferredAlertDateTime = DateUtil.newDateTime(DateUtil.today(), preferredAlertTime);
        Assert.assertTrue(preferredAlertDateTime.isAfter(DateUtil.now().plusMinutes(1))); // Assuming that schedule creation time will take atmost 1 min
        Assert.assertTrue(preferredAlertDateTime.isBefore(DateUtil.now().plusMinutes(5)));
    }

    @Test
    public void shouldNotEnrollMotherIfAlreadyEnrolledForTTVaccination(){
        DateTime edd = new DateTime(2012, 10, 10, 0, 0);
        String motherCaseId = "motherCaseId";

        EnrollmentRecord enrollmentRecord = new EnrollmentRecord(null, null, null, null, null, null, null, null, null, null);
        when(scheduleTrackingService.getEnrollment("motherCaseId", CareScheduleTrackingService.ttVaccinationScheduleName)).thenReturn(enrollmentRecord);
        careScheduleTrackingService.enroll(motherCaseId, edd);

        verify(scheduleTrackingService).getEnrollment("motherCaseId", CareScheduleTrackingService.ttVaccinationScheduleName);
        verify(scheduleTrackingService, never()).enroll(any(EnrollmentRequest.class));
    }

    @Test
    public void shouldEnrollChildForMeaslesVaccinationWhenAgeLessThan9Months(){
        DateTime dob = new DateTime(2012, 3, 10, 0, 0);
        String childCaseId = "childCaseId";

        when(scheduleTrackingService.getEnrollment("childCaseId", CareScheduleTrackingService.measlesVaccinationScheduleName)).thenReturn(null);
        careScheduleTrackingService.enrollChild(childCaseId, dob);

        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(scheduleTrackingService).enroll(captor.capture());
        verify(scheduleTrackingService).getEnrollment("childCaseId", CareScheduleTrackingService.measlesVaccinationScheduleName);

        EnrollmentRequest enrollmentRequest = captor.getValue();
        Assert.assertEquals(new LocalDate(2012, 3, 10).plusMonths(9), enrollmentRequest.getReferenceDate());
        Assert.assertEquals(childCaseId, enrollmentRequest.getExternalId());
        Assert.assertEquals(CareScheduleTrackingService.measlesVaccinationScheduleName, enrollmentRequest.getScheduleName());
        Assert.assertNotNull(enrollmentRequest.getEnrollmentDateTime());

    }

    @Test
    public void shouldEnrollChildForMeaslesVaccinationWhenAgeGreaterThan9Months(){
        DateTime dob = new DateTime(2011, 3, 10, 0, 0);
        String childCaseId = "childCaseId";

        when(scheduleTrackingService.getEnrollment("childCaseId", CareScheduleTrackingService.measlesVaccinationScheduleName)).thenReturn(null);
        careScheduleTrackingService.enrollChild(childCaseId, dob);

        ArgumentCaptor<EnrollmentRequest> captor = ArgumentCaptor.forClass(EnrollmentRequest.class);
        verify(scheduleTrackingService).enroll(captor.capture());
        verify(scheduleTrackingService).getEnrollment("childCaseId", CareScheduleTrackingService.measlesVaccinationScheduleName);

        EnrollmentRequest enrollmentRequest = captor.getValue();
        Assert.assertEquals(DateUtil.today(), enrollmentRequest.getReferenceDate());
        Assert.assertEquals(childCaseId, enrollmentRequest.getExternalId());
        Assert.assertEquals(CareScheduleTrackingService.measlesVaccinationScheduleName, enrollmentRequest.getScheduleName());
        Assert.assertNotNull(enrollmentRequest.getEnrollmentDateTime());

    }
}
