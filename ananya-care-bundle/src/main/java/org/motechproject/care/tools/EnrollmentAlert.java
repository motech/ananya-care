package org.motechproject.care.tools;

import org.motechproject.commons.date.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;

public class EnrollmentAlert {
    private Enrollment enrollment;
    private String nextAlertDetails;

    public EnrollmentAlert(Enrollment enrollment, String nextAlertDetails) {
        this.enrollment = enrollment;
        this.nextAlertDetails = nextAlertDetails;
    }


    public String getCurrentMilestoneName() {
        return enrollment.getCurrentMilestoneName();
    }

    public String getScheduleName() {
        return enrollment.getScheduleName();
    }

    public String getStatus() {
        return enrollment.getStatus().name();
    }

    public String getPreferredAlertTime() {
        Time preferredAlertTime = enrollment.getPreferredAlertTime();
        return preferredAlertTime != null ? preferredAlertTime.toString() : "";
    }

    public String getNextAlertDetails() {
        return nextAlertDetails;
    }

}
