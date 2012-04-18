package org.motechproject.care.schedule.vaccinations;

public enum VaccinationSchedule {

    TT("TT Vaccination"),


    Measles("Measles Vaccination"),
    Vita("Vita Vaccination"),
    Bcg("Bcg Vaccination");

    private String vaccinationScheduleName;

    VaccinationSchedule(String vaccinationScheduleName) {
        this.vaccinationScheduleName = vaccinationScheduleName;
    }

    public String getName() {
        return vaccinationScheduleName;
    }
}


