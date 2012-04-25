package org.motechproject.care.schedule.service;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleService {
    protected ScheduleTrackingService trackingService;
    Logger logger = Logger.getLogger(ScheduleService.class);

    @Autowired
    public ScheduleService(ScheduleTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    public void enroll(String caseId, DateTime referenceDate, String scheduleName) {
        if (isNotEnrolled(caseId,scheduleName)){
            logger.info(String.format("Enrolling client for external id : %s , schedule : %s", caseId, scheduleName));
            trackingService.enroll(enrollmentRequestFor(caseId, referenceDate.toLocalDate(),scheduleName));
        }
    }

    public void fulfillMileStone(String caseId, String milestoneName, DateTime measlesDate, String scheduleName) {
        if(isCurrentMilestone(caseId, milestoneName,scheduleName))
            fulfillCurrentMilestone(caseId,measlesDate,scheduleName);
    }

    private boolean isNotEnrolled(String caseId, String scheduleName) {
        return trackingService.getEnrollment(caseId, scheduleName) == null;
    }

    private boolean isCurrentMilestone(String caseId, String milestoneName, String scheduleName) {
        EnrollmentRecord enrollment = trackingService.getEnrollment(caseId, scheduleName);
        if(enrollment == null) return false;
        return enrollment.getCurrentMilestoneName().equals(milestoneName);
    }

    private void fulfillCurrentMilestone(String caseId, DateTime fulfillmentDateTime, String scheduleName) {
        LocalDate fulfillmentDate = fulfillmentDateTime.toLocalDate();
        Time fulfillmentTime = DateUtil.time(fulfillmentDateTime);
        logger.info(String.format("Fulfilling current milestone for external id : %s , schedule : %s", caseId, scheduleName));
        trackingService.fulfillCurrentMilestone(caseId, scheduleName,fulfillmentDate, fulfillmentTime );
    }

    private EnrollmentRequest enrollmentRequestFor(String caseId, LocalDate referenceDate, String scheduleName) {
        Time preferredAlertTime = DateUtil.time(DateTime.now().plusMinutes(2));
        LocalDate enrollmentDate = DateUtil.today();
        Time enrollmentTime = DateUtil.time(DateUtil.now());
        return new EnrollmentRequest(caseId, scheduleName, preferredAlertTime, referenceDate, null, enrollmentDate, enrollmentTime, null, null);
    }
}
