package org.motechproject.care.schedule.vaccinations;

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
}
