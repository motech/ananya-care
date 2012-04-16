package org.motechproject.care.schedule.vaccinations;

public enum VaccinationSchedule {


    Measles("Measles Vaccination");
    private String vaccinationScheduleName;


    VaccinationSchedule(String vaccinationScheduleName) {
        this.vaccinationScheduleName = vaccinationScheduleName;
    }

    public String getName() {
        return vaccinationScheduleName;
    }
}


