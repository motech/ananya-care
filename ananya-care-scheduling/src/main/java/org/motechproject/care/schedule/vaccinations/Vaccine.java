package org.motechproject.care.schedule.vaccinations;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class Vaccine {
    protected ScheduleTrackingService trackingService;
    private String scheduleName;

    @Autowired
    public Vaccine(ScheduleTrackingService trackingService, String scheduleName) {
        this.trackingService = trackingService;
        this.scheduleName = scheduleName;
    }

    public abstract void process(String caseId, DateTime dob);

    protected boolean isNotEnrolled(String caseId) {
        return trackingService.getEnrollment(caseId,scheduleName) == null;
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
