package org.motechproject.care.schedule.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CareScheduleTrackingService {

    public static final String ttVaccinationScheduleName = "TT Vaccination";
    private ScheduleTrackingService trackingService;

    @Autowired
    public CareScheduleTrackingService(ScheduleTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    public void enroll(String CaseId, DateTime edd) {
        if (edd == null) return;
        if (isNotEnrolled(CaseId, ttVaccinationScheduleName))
            trackingService.enroll(enrollmentRequestFor(CaseId, ttVaccinationScheduleName, edd));
    }

    private EnrollmentRequest enrollmentRequestFor(String CaseId, String vaccinationScheduleName, DateTime edd) {
        LocalDate referenceDate = edd.minusMonths(9).toLocalDate() ;
        Time preferredAlertTime = DateUtil.time(DateTime.now().plusMinutes(5));
        LocalDate enrollmentDate = DateUtil.today();
        Time enrollmentTime = DateUtil.time(DateUtil.now());
        return new EnrollmentRequest(CaseId, vaccinationScheduleName, preferredAlertTime, referenceDate, null, enrollmentDate, enrollmentTime, null, null);
    }

    private boolean isNotEnrolled(String CaseId, String vaccinationName) {
        return trackingService.getEnrollment(CaseId, vaccinationName) == null;
    }
}
