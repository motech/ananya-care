package org.motechproject.care.schedule.vaccinations;

import java.util.ArrayList;

public enum ExpirySchedule {
    ChildCare("Child Care"),
    MotherCare("Mother Care");

    private String vaccinationScheduleName;

    ExpirySchedule(String vaccinationScheduleName) {
        this.vaccinationScheduleName = vaccinationScheduleName;
    }

    public String getName() {
        return vaccinationScheduleName;
    }

    public static ArrayList<String> allVaccineNames() {
        ArrayList<String> vaccines = new ArrayList<String>();
        for (ExpirySchedule b : ExpirySchedule.values()) {
            vaccines.add(b.getName());
        }
        return vaccines;
    }
}
