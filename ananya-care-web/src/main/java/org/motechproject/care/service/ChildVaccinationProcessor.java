package org.motechproject.care.service;

import org.motechproject.care.domain.Child;
import org.motechproject.care.service.schedule.VaccinationService;

import java.util.List;

public class ChildVaccinationProcessor {

    protected List<VaccinationService> vaccinationServices;

    public ChildVaccinationProcessor(List<VaccinationService> vaccinationServices) {
        this.vaccinationServices = vaccinationServices;
    }


    public void enrollUpdateVaccines(Child child){
        for(VaccinationService vaccineService : vaccinationServices)
            vaccineService.process(child);
    }

    public List<VaccinationService> getVaccinationServices() {
        return vaccinationServices;
    }
}

