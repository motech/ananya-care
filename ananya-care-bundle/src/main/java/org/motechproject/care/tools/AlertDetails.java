package org.motechproject.care.tools;

import java.util.Date;

public class AlertDetails {

    public String getMilestoneName() {
        return milestoneName;
    }

    public String getWindowName() {
        return windowName;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    private final String milestoneName;
    private final String windowName;
    private final Date scheduledTime;

    public AlertDetails(String milestoneName, String windowName, Date scheduledTime) {
        this.milestoneName = milestoneName;
        this.windowName = windowName;
        this.scheduledTime = scheduledTime;
    }

    public String details() {
        return String.format("Milestone: %s; Window: %s; Time: %s", milestoneName, windowName, scheduledTime.toString());
    }

    public boolean isBefore(AlertDetails other) {
        if(other == null || other.scheduledTime == null) {
            return true;
        }
        return scheduledTime.before(other.scheduledTime);
    }
}
