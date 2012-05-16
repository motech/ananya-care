package org.motechproject.care.tools;

import org.joda.time.DateTime;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.Enrollment;
import org.motechproject.scheduletracking.api.domain.EnrollmentStatus;

public class EnrollmentAlert {
    private Enrollment enrollment;
    private DateTime dueWindowAlertTimings;
    private boolean showAlertWarning;

    public EnrollmentAlert(Enrollment enrollment, DateTime dueWindowAlertTimings) {
        this.enrollment = enrollment;
        this.dueWindowAlertTimings = dueWindowAlertTimings;
        this.showAlertWarning = shouldAlertHaveBeenRaisedTodayAtAPassedPreferredAlertTime();
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
        return dueWindowAlertTimings != null ? dueWindowAlertTimings.toString() : "";
    }

    public boolean getShowAlertWarning() {
        return showAlertWarning;
    }

    private boolean shouldAlertHaveBeenRaisedTodayAtAPassedPreferredAlertTime() {
        return enrollment.getStatus() == EnrollmentStatus.ACTIVE && dueWindowAlertTimings.isBeforeNow() && dueWindowAlertTimings.plusDays(1).isAfterNow();
    }

}
