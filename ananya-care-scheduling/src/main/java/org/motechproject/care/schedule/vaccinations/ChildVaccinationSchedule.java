package org.motechproject.care.schedule.vaccinations;

public enum ChildVaccinationSchedule {

    Measles("Measles Vaccination"),
    Vita("Vita Vaccination"),
    Bcg("Bcg Vaccination"),
    Hepatitis0("Hepatitis0 Vaccination"),
    Hepatitis("Hepatitis Vaccination"),
    DPT("DPT Vaccination"),
    OPV0("OPV0 Vaccination"),
    OPV("OPV Vaccination"),
    OPVBooster("OPVBooster Vaccination");

    private String vaccinationScheduleName;

    ChildVaccinationSchedule(String vaccinationScheduleName) {
        this.vaccinationScheduleName = vaccinationScheduleName;
    }

    public String getName() {
        return vaccinationScheduleName;
    }

    public static ChildVaccinationSchedule fromString(String name) {
        if (name != null) {
            for (ChildVaccinationSchedule b : ChildVaccinationSchedule.values()) {
                if (name.equalsIgnoreCase(b.getName())) {
                    return b;
                }
            }
        }
        return null;
    }
}


