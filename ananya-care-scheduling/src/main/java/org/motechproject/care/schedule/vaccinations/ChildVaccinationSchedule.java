package org.motechproject.care.schedule.vaccinations;

import java.util.ArrayList;

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

    public static ArrayList<String> allVaccineNames() {
        ArrayList<String> vaccines = new ArrayList<String>();
        for (ChildVaccinationSchedule b : ChildVaccinationSchedule.values()) {
            vaccines.add(b.getName());
        }
        return vaccines;
    }
}


