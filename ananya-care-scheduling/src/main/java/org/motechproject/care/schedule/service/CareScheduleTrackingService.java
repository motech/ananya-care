package org.motechproject.care.schedule.service;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.service.EnrollmentRequest;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CareScheduleTrackingService {

    public static final String ttVaccinationScheduleName = "TT Vaccination";
    public static final String measlesVaccinationScheduleName = "Measles Vaccination";
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

    private EnrollmentRequest enrollmentRequestFor(String CaseId, String vaccinationScheduleName, DateTime dob) {
        LocalDate referenceDate = calculateReferenceDate(vaccinationScheduleName, dob);
        Time preferredAlertTime = DateUtil.time(DateTime.now().plusMinutes(5));
        LocalDate enrollmentDate = DateUtil.today();
        Time enrollmentTime = DateUtil.time(DateUtil.now());

        return new EnrollmentRequest(CaseId, vaccinationScheduleName, preferredAlertTime, referenceDate, null, enrollmentDate, enrollmentTime, null, null);
    }

    private LocalDate calculateReferenceDate(String vaccinationScheduleName, DateTime dob) {
        if(vaccinationScheduleName.equals(measlesVaccinationScheduleName))
            return getAgeInMoths(dob) >= 0.75 ? DateUtil.today() : dob.plusMonths(9).toLocalDate();
        return DateUtil.today();
    }

    private boolean isNotEnrolled(String CaseId, String vaccinationName) {
        return trackingService.getEnrollment(CaseId, vaccinationName) == null;
    }

    private float getAgeInMoths(DateTime dob) {
        Period period = new Period(dob.toLocalDate(), DateUtil.today(), PeriodType.months());
        return period.getMonths()/12;
    }

    public void enrollChild(String caseId, DateTime dob) {
        if (isNotEnrolled(caseId, measlesVaccinationScheduleName))
        trackingService.enroll(enrollmentRequestFor(caseId,measlesVaccinationScheduleName, dob));
    }
}
