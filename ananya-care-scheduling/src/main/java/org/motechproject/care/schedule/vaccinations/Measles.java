package org.motechproject.care.schedule.vaccinations;

import org.joda.time.DateTime;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Measles extends Vaccine {

    @Autowired
    public Measles(ScheduleTrackingService trackingService) {
        super(trackingService,VaccinationSchedule.Measles.getName());
    }

    @Override
    public void process(String caseId, DateTime dob) {
        if (isNotEnrolled(caseId))
        trackingService.enroll(enrollmentRequestFor(caseId, dob.toLocalDate()));
    }
}
