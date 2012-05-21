package org.motechproject.care.tools;

import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;

public class EnrollmentAlert {
    private Enrollment enrollment;
    private String dueWindowAlertTimings;

    public EnrollmentAlert(Enrollment enrollment, String dueWindowAlertTimings) {
        this.enrollment = enrollment;
        this.dueWindowAlertTimings = dueWindowAlertTimings;
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

    public String getDueWindowAlertTimings() {
        return dueWindowAlertTimings;
    }

}
