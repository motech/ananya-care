package org.motechproject.care.schedule.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class SchedulerService {
    protected ScheduleTrackingService trackingService;
    private String scheduleName;

    @Autowired
    public SchedulerService(ScheduleTrackingService trackingService, String scheduleName) {
        this.trackingService = trackingService;
        this.scheduleName = scheduleName;
    }

    protected boolean isNotEnrolled(String caseId) {
        return trackingService.getEnrollment(caseId,scheduleName) == null;
    }

    protected boolean isCurrentMilestone(String caseId, String milestoneName) {
        EnrollmentRecord enrollment = trackingService.getEnrollment(caseId, scheduleName);
        if(enrollment == null) return false;
        return enrollment.getCurrentMilestoneName().equals(milestoneName);
    }

    protected void fulfillCurrentMilestone(String caseId, DateTime fulfillmentDateTime) {
        LocalDate fulfillmentDate = fulfillmentDateTime.toLocalDate();
        Time fulfillmentTime = DateUtil.time(fulfillmentDateTime);
        trackingService.fulfillCurrentMilestone(caseId, scheduleName,fulfillmentDate, fulfillmentTime );
    }

    protected EnrollmentRequest enrollmentRequestFor(String caseId, LocalDate referenceDate) {
        Time preferredAlertTime = DateUtil.time(DateTime.now().plusMinutes(5));
        LocalDate enrollmentDate = DateUtil.today();
        Time enrollmentTime = DateUtil.time(DateUtil.now());
        return new EnrollmentRequest(caseId, scheduleName, preferredAlertTime, referenceDate, null, enrollmentDate, enrollmentTime, null, null);
    }

    public String getScheduleName() {
        return scheduleName;
    }
}
