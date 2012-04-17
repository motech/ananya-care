package org.motechproject.care.schedule.service;

import org.joda.time.DateTime;
import org.motechproject.care.schedule.vaccinations.VaccinationSchedule;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MeaslesSchedulerService extends SchedulerService {
    
    public static String milestone = "Measles";

    @Autowired
    public MeaslesSchedulerService(ScheduleTrackingService trackingService) {
        super(trackingService, VaccinationSchedule.Measles.getName());
    }

    public void enroll(String caseId, DateTime dob) {
        if (isNotEnrolled(caseId))
            trackingService.enroll(enrollmentRequestFor(caseId, dob.toLocalDate()));
    }

    public void fulfilMeaslesTaken(String caseId, DateTime measlesDate) {
        if(isCurrentMilestone(caseId, milestone))
            fulfillCurrentMilestone(caseId,measlesDate);
    }

}
