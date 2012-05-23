package org.motechproject.care.schedule.vaccinations;

public enum MotherVaccinationSchedule {
    TT("TT Vaccination"),
    TTBooster("TT Booster"),
    Anc("Anc Visit"),
    Anc4("Anc4 Visit");

    private String vaccinationScheduleName;

    MotherVaccinationSchedule(String vaccinationScheduleName) {
        this.vaccinationScheduleName = vaccinationScheduleName;
    }

    public String getName() {
        return vaccinationScheduleName;
    }
}

