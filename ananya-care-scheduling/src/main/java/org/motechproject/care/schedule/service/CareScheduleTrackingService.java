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
public class CareScheduleTrackingService  {

    public static final String ttVaccinationScheduleName = "TT Vaccination";
    private ScheduleTrackingService trackingService;

    @Autowired
    public CareScheduleTrackingService(ScheduleTrackingService trackingService){
        this.trackingService = trackingService;
    }

    public void enrollMother(String motherCaseId, DateTime edd) {
        if(edd == null) return;
        if(isNotEnrolled(motherCaseId))
            trackingService.enroll(enrollmentRequestForTT(motherCaseId, edd));
    }

    private EnrollmentRequest enrollmentRequestForTT(String motherCaseId, DateTime edd) {
        LocalDate referenceDate = new LocalDate(edd.minusWeeks(36));
        LocalDate enrollmentDate = DateUtil.today();
        Time enrollmentTime = DateUtil.time(DateUtil.now());

        return new EnrollmentRequest(motherCaseId, ttVaccinationScheduleName, null, referenceDate, null, enrollmentDate, enrollmentTime,null,null);
    }

    private boolean isNotEnrolled(String motherCaseId) {
        return trackingService.getEnrollment(motherCaseId, ttVaccinationScheduleName) == null;
    }

}
