package org.motechproject.care.tools;


public class NullAlertDetails extends AlertDetails {
    public NullAlertDetails() {
        super(null, null, null);
    }

    @Override
    public String details() {
        return "No alert are scheduled in the quartz queue for this milestone.";
    }

    @Override
    public boolean isBefore(AlertDetails other) {
        return false;
    }
}
