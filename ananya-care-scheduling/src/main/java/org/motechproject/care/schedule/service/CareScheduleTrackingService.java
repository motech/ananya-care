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

    private ScheduleTrackingService trackingService;

    @Autowired
    public CareScheduleTrackingService(ScheduleTrackingService trackingService){
        this.trackingService = trackingService;
    }

    public void enrollMother(String motherCaseId, DateTime edd) {
        trackingService.enroll(enrollmentRequest(motherCaseId, edd));
    }

    private EnrollmentRequest enrollmentRequest(String motherCaseId, DateTime edd) {
        String scheduleName = "TT Vaccination";
        Time preferredAlertTime = DateUtil.time(DateUtil.now().plusMinutes(1));
        LocalDate referenceDate = DateUtil.today();
        Time referenceTime = DateUtil.time(DateUtil.now());
        LocalDate enrollmentDate = DateUtil.today();
        Time enrollmentTime = DateUtil.time(DateUtil.now());

        return new EnrollmentRequest(motherCaseId, scheduleName, preferredAlertTime, referenceDate, referenceTime, enrollmentDate, enrollmentTime,null,null);
    }

}
