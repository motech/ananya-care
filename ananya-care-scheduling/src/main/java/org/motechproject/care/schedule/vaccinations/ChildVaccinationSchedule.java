package org.motechproject.care.schedule.vaccinations;

public enum ChildVaccinationSchedule {

    Measles("Measles Vaccination"),
    Vita("Vita Vaccination"),
    Bcg("Bcg Vaccination"),
    Hepatitis0("Hepatitis0 Vaccination"),
    Hepatitis("Hepatitis Vaccination"),
    DPT("DPT Vaccination"),
    DPTBooster("DPTBooster Vaccination"),
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
}


