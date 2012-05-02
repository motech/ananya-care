package org.motechproject.care.schedule.vaccinations;

public enum MotherVaccinationSchedule {
    TT("TT Vaccination"),
    TTBooster("TT Booster"),
    Anc("Anc Visit"),
    Anc4("Anc4 Visit"),
    MotherCare("Mother Care");

    private String vaccinationScheduleName;

    MotherVaccinationSchedule(String vaccinationScheduleName) {
        this.vaccinationScheduleName = vaccinationScheduleName;
    }

    public String getName() {
        return vaccinationScheduleName;
    }

    public static MotherVaccinationSchedule fromString(String name) {
        if (name != null) {
            for (MotherVaccinationSchedule b : MotherVaccinationSchedule.values()) {
                if (name.equalsIgnoreCase(b.getName())) {
                    return b;
                }
            }
        }
        return null;
    }

}
