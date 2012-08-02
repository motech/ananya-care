package org.motechproject.care.schedule.service;

import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRecord;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.EnrollmentsQuery;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class ScheduleService {
    protected ScheduleTrackingService trackingService;
    Logger logger = Logger.getLogger(ScheduleService.class);

    @Autowired
    public ScheduleService(ScheduleTrackingService trackingService) {
        this.trackingService = trackingService;
    }

    public void enroll(String caseId, DateTime referenceDate, String scheduleName) {
        if (isNotEnrolled(caseId, scheduleName)) {
            logger.info(String.format("Enrolling client for external id : %s , schedule : %s", caseId, scheduleName));
            trackingService.enroll(enrollmentRequestFor(caseId, referenceDate.toLocalDate(), scheduleName));
        }
    }

    public EnrollmentRecord unenroll(String caseId, String scheduleName) {
        EnrollmentRecord activeEnrollment = trackingService.getEnrollment(caseId, scheduleName);
        trackingService.unenroll(caseId, Arrays.asList(scheduleName));
        logger.info(String.format("Un-enrolled client for external id : %s , schedule : %s", caseId, scheduleName));

        if (activeEnrollment != null)
            return activeEnrollment;
        return getAnEnrollmentFor(caseId, scheduleName);

    }

    public void fulfillMilestone(String caseId, String milestoneName, DateTime vaccinationTakenDate, String scheduleName) {
        if (isCurrentMilestone(caseId, milestoneName, scheduleName))
            fulfillCurrentMilestone(caseId, vaccinationTakenDate, scheduleName);
    }

    private EnrollmentRecord getAnEnrollmentFor(String externalId, String scheduleName) {
        EnrollmentsQuery enrollmentsQuery = new EnrollmentsQuery()
                .havingExternalId(externalId)
                .havingSchedule(scheduleName);

        List<EnrollmentRecord> enrollmentRecords = trackingService.search(enrollmentsQuery);
        if (enrollmentRecords.size() >= 1)
            return enrollmentRecords.get(0);
        return null;
    }

    private boolean isNotEnrolled(String externalId, String scheduleName) {
        EnrollmentsQuery query = new EnrollmentsQuery()
                .havingExternalId(externalId)
                .havingSchedule(scheduleName);

        if (trackingService.search(query).size() >= 1)
            return false;
        return true;
    }

    private boolean isCurrentMilestone(String caseId, String milestoneName, String scheduleName) {
        EnrollmentRecord enrollment = trackingService.getEnrollment(caseId, scheduleName);
        if (enrollment == null) return false;
        return enrollment.getCurrentMilestoneName().equals(milestoneName);
    }

    private void fulfillCurrentMilestone(String caseId, DateTime fulfillmentDate, String scheduleName) {
        DateTime nowPlus2Minutes = DateTime.now().plusMinutes(2);
        DateTime fulfillmentDateTime = nowPlus2Minutes.withDate(fulfillmentDate.getYear(), fulfillmentDate.getMonthOfYear(), fulfillmentDate.getDayOfMonth());

        logger.info(String.format("Fulfilling current milestone for external id : %s , schedule : %s", caseId, scheduleName));
        trackingService.fulfillCurrentMilestone(caseId, scheduleName, fulfillmentDateTime.toLocalDate(), DateUtil.time(fulfillmentDateTime));
    }

    private EnrollmentRequest enrollmentRequestFor(String caseId, LocalDate referenceDate, String scheduleName) {
        DateTime now = DateTime.now();
        Time referenceTime = DateUtil.time(now.plusMinutes(2));
        LocalDate enrollmentDate = DateUtil.today();
        Time enrollmentTime = referenceTime;
        return new EnrollmentRequest(caseId, scheduleName, null, referenceDate, referenceTime, enrollmentDate, enrollmentTime, null, null);
    }
}
